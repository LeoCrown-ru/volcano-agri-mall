import request from '@/utils/request'

export function getPersonalRecommendations(userId, limit) {
  return request({
    url: '/ums/recommend/personal',
    method: 'get',
    params: { userId, limit }
  })
}

export function getRecommendationsByOrders(userId, limit) {
  return request({
    url: '/ums/recommend/byOrders',
    method: 'get',
    params: { userId, limit }
  })
}

export function getRecommendationsByFavorites(userId, limit) {
  return request({
    url: '/ums/recommend/byFavorites',
    method: 'get',
    params: { userId, limit }
  })
}

export function getRecommendationsByHistory(userId, limit) {
  return request({
    url: '/ums/recommend/byHistory',
    method: 'get',
    params: { userId, limit }
  })
}

export function getProductSimilarity(productId) {
  return request({
    url: '/ums/recommend/productSimilarity',
    method: 'get',
    params: { productId }
  })
}