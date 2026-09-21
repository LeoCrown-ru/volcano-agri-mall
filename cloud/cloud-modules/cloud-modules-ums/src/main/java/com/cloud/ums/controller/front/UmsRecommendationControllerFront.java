package com.cloud.ums.controller.front;

import com.cloud.ums.service.IRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个性化推荐前端控制器
 */
@RestController
@RequestMapping("/ums/recommend")
public class UmsRecommendationControllerFront {

    @Autowired
    private IRecommendationService recommendationService;

    @GetMapping("/personal")
    public Map<String, Object> getPersonalRecommendations(
            @RequestParam(value = "userId", defaultValue = "1") Long userId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<Long> recommendations = recommendationService.getRecommendations(userId, limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", recommendations);
        return result;
    }

    @GetMapping("/byOrders")
    public Map<String, Object> getRecommendationsByOrders(
            @RequestParam(value = "userId", defaultValue = "1") Long userId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<Long> recommendations = recommendationService.recommendByOrders(userId, limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", recommendations);
        return result;
    }

    @GetMapping("/byFavorites")
    public Map<String, Object> getRecommendationsByFavorites(
            @RequestParam(value = "userId", defaultValue = "1") Long userId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<Long> recommendations = recommendationService.recommendByFavorites(userId, limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", recommendations);
        return result;
    }

    @GetMapping("/byHistory")
    public Map<String, Object> getRecommendationsByHistory(
            @RequestParam(value = "userId", defaultValue = "1") Long userId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        List<Long> recommendations = recommendationService.recommendByHistory(userId, limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", recommendations);
        return result;
    }

    @GetMapping("/productSimilarity")
    public Map<String, Object> getProductSimilarity(
            @RequestParam("productId") Long productId) {
        
        Map<Long, Double> similarity = recommendationService.getProductSimilarity(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", similarity);
        return result;
    }
}