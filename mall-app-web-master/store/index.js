import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    hasLogin: false,
    userInfo: {
      nickname: '',
      icon: '',
      integration: 0, // 积分
      growth: 0        // 成长值
    },
  },
  mutations: {
    login(state, provider) {
      // 1. 标记登录状态为 true
      state.hasLogin = true;
      
      // 2. 关键修复：从传入的 provider 中获取用户信息（而非未定义的 userInfo）
      // 假设 provider 是后端返回的用户信息对象（包含 nickname、icon 等字段）
      state.userInfo = { ...state.userInfo, ...provider };
      
      // 3. 缓存用户信息（使用合并后的完整 userInfo）
      uni.setStorage({
        key: 'userInfo',  
        data: state.userInfo  // 建议存储合并后的 userInfo，而非原始 provider
      });
      
      console.log('登录后用户信息：', state.userInfo);
    },
    logout(state) {
      state.hasLogin = false;
      state.userInfo = {
        nickname: '',
        icon: '',
        integration: 0,
        growth: 0
      };
      uni.removeStorage({ key: 'userInfo' });
      uni.removeStorage({ key: 'token' });
    },
    // 更新积分
    updateIntegration(state, integration) {
      state.userInfo.integration = integration;
      // 同步更新缓存（可选，确保刷新后数据一致）
      uni.setStorage({ key: 'userInfo', data: state.userInfo });
    },
    // 更新成长值
    updateGrowth(state, growth) {
      state.userInfo.growth = growth;
      // 同步更新缓存（可选）
      uni.setStorage({ key: 'userInfo', data: state.userInfo });
    },
  },
  actions: {}
})

export default store