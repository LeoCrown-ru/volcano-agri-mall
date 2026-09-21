package com.cloud.ums.service.impl;

import com.cloud.ums.service.IRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐服务实现类
 */
@Service
public class RecommendationServiceImpl implements IRecommendationService {

    private static final String USER_PREFERENCE_KEY = "recommend:user_preference:";
    private static final String PRODUCT_SIMILARITY_KEY = "recommend:product_similarity:";
    private static final String USER_SIMILARITY_KEY = "recommend:user_similarity:";

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Long> getRecommendations(Long userId, int limit) {
        List<Long> orderRecommendations = recommendByOrders(userId, limit);
        List<Long> favoriteRecommendations = recommendByFavorites(userId, limit);
        List<Long> historyRecommendations = recommendByHistory(userId, limit);

        Map<Long, Double> scores = new HashMap<>();
        double orderWeight = 0.5;
        double favoriteWeight = 0.3;
        double historyWeight = 0.2;

        for (int i = 0; i < orderRecommendations.size(); i++) {
            Long productId = orderRecommendations.get(i);
            scores.merge(productId, (1.0 - i * 0.1) * orderWeight, Double::sum);
        }

        for (int i = 0; i < favoriteRecommendations.size(); i++) {
            Long productId = favoriteRecommendations.get(i);
            scores.merge(productId, (1.0 - i * 0.1) * favoriteWeight, Double::sum);
        }

        for (int i = 0; i < historyRecommendations.size(); i++) {
            Long productId = historyRecommendations.get(i);
            scores.merge(productId, (1.0 - i * 0.1) * historyWeight, Double::sum);
        }

        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> recommendByOrders(Long userId, int limit) {
        List<Long> purchasedProducts = getPurchasedProducts(userId);
        return recommendByItemBasedCF(purchasedProducts, limit);
    }

    @Override
    public List<Long> recommendByFavorites(Long userId, int limit) {
        List<Long> favoriteProducts = getFavoriteProducts(userId);
        return recommendByItemBasedCF(favoriteProducts, limit);
    }

    @Override
    public List<Long> recommendByHistory(Long userId, int limit) {
        List<Long> historyProducts = getHistoryProducts(userId);
        return recommendByItemBasedCF(historyProducts, limit);
    }

    @Override
    public Map<Long, Double> getUserSimilarity(Long userId) {
        Map<Long, Double> similarityMap = new HashMap<>();
        List<Long> userProducts = getUserAllProducts(userId);
        
        for (Long otherUserId : getAllUserIds()) {
            if (!otherUserId.equals(userId)) {
                List<Long> otherUserProducts = getUserAllProducts(otherUserId);
                double similarity = calculateCosineSimilarity(userProducts, otherUserProducts);
                if (similarity > 0.1) {
                    similarityMap.put(otherUserId, similarity);
                }
            }
        }
        
        return similarityMap;
    }

    @Override
    public Map<Long, Double> getProductSimilarity(Long productId) {
        String cacheKey = PRODUCT_SIMILARITY_KEY + productId;
        if (redisTemplate != null && redisTemplate.hasKey(cacheKey)) {
            return (Map<Long, Double>) redisTemplate.opsForValue().get(cacheKey);
        }

        Map<Long, Double> similarityMap = new HashMap<>();
        Set<Long> allProductIds = getAllProductIds();
        
        for (Long otherProductId : allProductIds) {
            if (!otherProductId.equals(productId)) {
                double similarity = calculateProductSimilarity(productId, otherProductId);
                if (similarity > 0.1) {
                    similarityMap.put(otherProductId, similarity);
                }
            }
        }

        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(cacheKey, similarityMap, 3600);
        }

        return similarityMap;
    }

    private List<Long> recommendByItemBasedCF(List<Long> purchasedProducts, int limit) {
        Map<Long, Double> recommendationScores = new HashMap<>();

        for (Long productId : purchasedProducts) {
            Map<Long, Double> similarProducts = getProductSimilarity(productId);
            
            for (Map.Entry<Long, Double> entry : similarProducts.entrySet()) {
                Long similarProductId = entry.getKey();
                double similarity = entry.getValue();
                
                if (!purchasedProducts.contains(similarProductId)) {
                    recommendationScores.merge(similarProductId, similarity, Double::sum);
                }
            }
        }

        return recommendationScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateProductSimilarity(Long productId1, Long productId2) {
        Set<Long> usersBoughtProduct1 = getUsersWhoBoughtProduct(productId1);
        Set<Long> usersBoughtProduct2 = getUsersWhoBoughtProduct(productId2);

        Set<Long> intersection = new HashSet<>(usersBoughtProduct1);
        intersection.retainAll(usersBoughtProduct2);

        double unionSize = usersBoughtProduct1.size() + usersBoughtProduct2.size() - intersection.size();
        
        if (unionSize == 0) {
            return 0.0;
        }

        return (double) intersection.size() / unionSize;
    }

    private double calculateCosineSimilarity(List<Long> list1, List<Long> list2) {
        Set<Long> set1 = new HashSet<>(list1);
        Set<Long> set2 = new HashSet<>(list2);

        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        double dotProduct = intersection.size();
        double magnitude1 = Math.sqrt(set1.size());
        double magnitude2 = Math.sqrt(set2.size());

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }

    private List<Long> getPurchasedProducts(Long userId) {
        List<Long> products = new ArrayList<>();
        try {
            if (redisTemplate != null) {
                String key = USER_PREFERENCE_KEY + userId + ":orders";
                products = (List<Long>) redisTemplate.opsForValue().get(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products != null ? products : new ArrayList<>();
    }

    private List<Long> getFavoriteProducts(Long userId) {
        List<Long> products = new ArrayList<>();
        try {
            if (redisTemplate != null) {
                String key = USER_PREFERENCE_KEY + userId + ":favorites";
                products = (List<Long>) redisTemplate.opsForValue().get(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products != null ? products : new ArrayList<>();
    }

    private List<Long> getHistoryProducts(Long userId) {
        List<Long> products = new ArrayList<>();
        try {
            if (redisTemplate != null) {
                String key = USER_PREFERENCE_KEY + userId + ":history";
                products = (List<Long>) redisTemplate.opsForValue().get(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products != null ? products : new ArrayList<>();
    }

    private List<Long> getUserAllProducts(Long userId) {
        List<Long> allProducts = new ArrayList<>();
        allProducts.addAll(getPurchasedProducts(userId));
        allProducts.addAll(getFavoriteProducts(userId));
        allProducts.addAll(getHistoryProducts(userId));
        return allProducts.stream().distinct().collect(Collectors.toList());
    }

    private Set<Long> getAllUserIds() {
        Set<Long> userIds = new HashSet<>();
        for (long i = 1; i <= 100; i++) {
            userIds.add(i);
        }
        return userIds;
    }

    private Set<Long> getAllProductIds() {
        Set<Long> productIds = new HashSet<>();
        for (long i = 1; i <= 1000; i++) {
            productIds.add(i);
        }
        return productIds;
    }

    private Set<Long> getUsersWhoBoughtProduct(Long productId) {
        Set<Long> users = new HashSet<>();
        for (long i = 1; i <= 100; i++) {
            if (Math.random() > 0.8) {
                users.add(i);
            }
        }
        return users;
    }
}