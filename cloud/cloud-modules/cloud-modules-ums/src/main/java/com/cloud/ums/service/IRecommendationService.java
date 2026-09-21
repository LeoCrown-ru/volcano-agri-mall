package com.cloud.ums.service;

import java.util.List;
import java.util.Map;

/**
 * 推荐服务接口
 */
public interface IRecommendationService {

    /**
     * 根据用户ID获取推荐商品列表
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 推荐商品ID列表
     */
    List<Long> getRecommendations(Long userId, int limit);

    /**
     * 基于用户的订单记录推荐
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 推荐商品ID列表
     */
    List<Long> recommendByOrders(Long userId, int limit);

    /**
     * 基于用户的收藏记录推荐
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 推荐商品ID列表
     */
    List<Long> recommendByFavorites(Long userId, int limit);

    /**
     * 基于用户的浏览历史推荐
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 推荐商品ID列表
     */
    List<Long> recommendByHistory(Long userId, int limit);

    /**
     * 获取用户相似度（基于物品的协同过滤）
     * @param userId 用户ID
     * @return 用户相似度Map
     */
    Map<Long, Double> getUserSimilarity(Long userId);

    /**
     * 获取商品相似度（基于物品的协同过滤）
     * @param productId 商品ID
     * @return 商品相似度Map
     */
    Map<Long, Double> getProductSimilarity(Long productId);
}