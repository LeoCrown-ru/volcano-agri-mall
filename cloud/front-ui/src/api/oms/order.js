import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/oms/front/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(id) {
  return request({
    url: '/oms/front/order/' + id,
    method: 'get'
  })
}

// 初始化订单
export function initOrder(data) {
  return request({
    url: '/oms/front/order/initOrder',
    method: 'post',
    data: data
  })
}

// 下单
export function placeOrder(data) {
  return request({
    url: '/oms/front/order/placeOrder',
    method: 'post',
    data: data
  })
}

// 取消订单
export function cancelOrder(data) {
  return request({
    url: '/oms/front/order',
    method: 'put',
    data: data
  })
}

// 删除订单（支持批量，ids 逗号分隔）
export function delOrder(ids) {
  return request({
    url: '/oms/front/order/' + ids,
    method: 'delete'
  })
}


