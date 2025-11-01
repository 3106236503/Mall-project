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

// 充值会员积分
export function updateIntegration(id, params) {
  return request({
    url: '/member/recharge',
    method: 'post',
    params: {
      id: id,
      integration: params.integration,
      operateNote: params.operateNote // 备注参数
    }
  })
}

// 适配后端分页接口，传递 pageNum、pageSize 参数
export function fetchPointRecord(params) {
  return request({
    url: '/member/history',
    method: 'GET',
    params: {
      memberId: params.memberId,
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10
    }
  });
}



