import request from '@/utils/request'

// 查询所有会员等级
export function fetchList(params) {
  return request({
    url: '/memberLevel/list',
    method: 'get',
    params: params
  })
}



// 添加会员等级
export function createMemberLevel(data) {
  return request({
    url: '/memberLevel/create',
    method: 'post',
    data: data
  })
}

// 修改会员等级信息
export function updateMemberLevel(data) {
  return request({
    url: '/memberLevel/update',
    method: 'post',
    data: data
  })
}

// 删除会员等级信息
export function deleteMemberLevel(id) {
  return request({
    url: `/memberLevel/delete/${id}`,
    method: 'post'
  })
}
// 更新会员等级状态
export function updateMemberLevelStatus(id, defaultStatus) {
  return request({
    url: `/memberLevel/updateStatus/${id}`,
    method: 'post',
    params: {
      defaultStatus: defaultStatus
    }
  })
}


//可用会员等级列表
export function fetchAvailableList(params) {
  return request({
    url: '/memberLevel/listAvailable',
    method: 'get',
    params: params
  })
}
