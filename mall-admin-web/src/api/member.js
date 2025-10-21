import request from '@/utils/request'

// 获取会员列表
export function fetchList(params) {
  return request({
    url: '/member/listAll',
    method: 'get',
    params: params
  })
}

// 添加会员
export function createMember(data) {
  return request({
    url: '/member/create',
    method: 'post',
    data: data
  })
}

// 更新会员
export function updateMember(data) {
  return request({
    url: '/member/update',
    method: 'post',
    data: data
  })
}

// 删除会员
export function deleteMember(id) {
  return request({
    url: '/member/delete/',
    method: 'post',
    params: { id: id } // 将id作为查询参数传递
  })
}

// 更新会员状态
export function updateStatus(id, params) {
  return request({
    url: '/member/updateStatus/' + id,
    method: 'post',
    params: params
  })
}


// 获取会员详情
export function getMemberInfo(id) {
  return request({
    url: '/member/detail',
    method: 'get',
    params: { id }
  })
}


