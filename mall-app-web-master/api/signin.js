// 签到相关 API 接口
import request from '@/utils/requestUtil'

/**
 * 查询今日签到状态
 * @returns {Promise} 签到状态
 */
export function getTodaySignInStatus() {
    return request({
        url: '/sso/signIn/todayStatus',
        method: 'get'
    });
}

/**
 * 执行签到
 * @returns {Promise} 签到结果
 */
export function doSignIn() {
    return request({
        url: '/sso/signIn/signIn',
        method: 'post'
    });
}

/**
 * 执行补签
 * @param {string} signInDate 补签日期，格式：yyyy-MM-dd
 * @returns {Promise} 补签结果
 */
export function doMakeUpSignIn(signInDate) {
    return request({
        url: '/sso/signIn/makeUp',
        method: 'post',
        data: { signInDate }
    });
}

/**
 * 查询签到记录
 * @param {number} year 年份
 * @param {number} month 月份
 * @returns {Promise} 签到记录列表
 */
export function getSignInRecords(year, month) {
    return request({
        url: '/sso/signIn/records',
        method: 'get',
        params: { year, month }
    });
}

/**
 * 查询连续签到天数
 * @returns {Promise} 连续签到天数
 */
export function getContinuousSignInDays() {
    return request({
        url: '/sso/signIn/continuousDays',
        method: 'get'
    });
}




/**
 * 查询会员积分和成长值
 * 对应后端 @RequestMapping("/points") 接口
 */
export function getMemberPoints() {
  return request({
    url: '/sso/signIn/points', // 后端接口路径，需与实际部署路径一致
    method: 'GET'
  });
}


/**
 * 查询会员签到记录
 * 对应后端 @RequestMapping("/reecords") 接口
 */
export function getMemberSignInRecords() {
  return request({
    url: '/sso/signIn/reecords', // 按用户提供的后端接口路径（注意：可能是"records"的拼写错误，需与后端保持一致）
    method: 'GET'
  });
}


// 新增：查询累计签到总天数（解决报错的核心）
/**
 * 查询累计签到总天数
 * @returns {Promise} 累计签到总天数
 */
export function getTotalSignDays() {
    return request({
        url: '/sso/signIn/totalDays', // 需与后端"查询累计签到天数"接口路径一致
        method: 'get'
    });
}