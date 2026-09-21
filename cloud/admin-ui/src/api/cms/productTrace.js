import request from '@/utils/request'

// 查询商品溯源列表
export function listProductTrace(query) {
  return request({
    url: '/cms/productTrace/list',
    method: 'get',
    params: query
  })
}

// 查询商品溯源详细
export function getProductTrace(id) {
  return request({
    url: '/cms/productTrace/' + id,
    method: 'get'
  })
}

// 新增商品溯源
export function addProductTrace(data) {
  return request({
    url: '/cms/productTrace',
    method: 'post',
    data: data
  })
}

// 修改商品溯源
export function updateProductTrace(data) {
  return request({
    url: '/cms/productTrace',
    method: 'put',
    data: data
  })
}

// 删除商品溯源
export function delProductTrace(id) {
  return request({
    url: '/cms/productTrace/' + id,
    method: 'delete'
  })
}
