import request from '@/utils/request'

// 根据商品ID查询溯源信息
export function getProductTraceByProductId(productId) {
  return request({
    url: '/cms/front/productTrace/getByProductId/' + productId,
    method: 'get'
  })
}
