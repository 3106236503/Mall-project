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

			state.hasLogin = true;
		    state.userInfo = { ...state.userInfo, ...userInfo };
			uni.setStorage({//缓存用户登陆状态
			    key: 'userInfo',  
			    data: provider  
			}) 
			console.log(state.userInfo);
		},
		logout(state) {
			state.hasLogin = false;
			state.userInfo = {};
			uni.removeStorage({  
                key: 'userInfo'  
            });
			uni.removeStorage({
			    key: 'token'  
			})
		},
		 // 更新积分（原有）
		    updateIntegration(state, integration) {
		      state.userInfo.integration = integration;
		    },
		    // 更新成长值（新增）
		    updateGrowth(state, growth) {
		      state.userInfo.growth = growth;
		    },
	},
	actions: {
	
	}
})

export default store
