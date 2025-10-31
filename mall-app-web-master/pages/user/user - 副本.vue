<template>  
    <view class="container">  
        <!-- 用户头部区域 -->
		<view class="user-section">
			<image class="bg" src="/static/user-bg.jpg" mode="widthFix"></image>
			<view class="user-mask"></view>
			<view class="user-info-box">
				<view class="portrait-shadow">
					<view class="portrait-ring"></view>
					<image class="portrait" :src="userInfo.icon || '/static/missing-face.png'" mode="widthFix" @click="handleAvatarClick"></image>
				</view>
				<view class="info-box">
					<text class="username">{{userInfo.nickname || '游客'}}</text>
					<text class="login-tag" v-if="hasLogin">已登录</text>
					<text class="login-tag unlogin" v-else>未登录</text>
				</view>
			</view>
			
			<!-- VIP卡片 -->
			<view class="vip-card-box">
				<image class="card-bg" src="/static/vip-card-bg.png" mode="widthFix"></image>
				<view class="card-decor"></view>
				<view class="b-btn" hover-class="b-btn-hover">立即开通</view>
				<view class="tit">
					<text class="yticon icon-iLinkapp-"></text>黄金会员
				</view>
				<text class="e-m">mall移动端商城</text>
				<text class="e-b">黄金及以上会员可享有会员价优惠商品</text>
			</view>
		</view>
		
		<!-- 内容容器 -->
		<view class="cover-container">
			<image class="arc" src="/static/arc.png" mode="widthFix"></image>
			
			<!-- 积分成长值区域 -->
			<view class="tj-sction">
				<view class="tj-item" hover-class="tj-item-hover">
					<text class="tj-icon">💎</text>
					<text class="num">{{userInfo.integration || '0'}}</text>
					<text class="label">积分</text>
				</view>
				<view class="tj-item" hover-class="tj-item-hover">
					<text class="tj-icon">🌱</text>
					<text class="num">{{userInfo.growth || '0'}}</text>
					<text class="label">成长值</text>
				</view>
				<view class="tj-item" @click="navTo('/pages/coupon/couponList')" hover-class="tj-item-hover">
					<text class="tj-icon">🎫</text>
					<text class="num">{{couponCount || '0'}}</text>
					<text class="label">优惠券</text>
				</view>
			</view>

			<!-- 签到+补签区域 -->
			<view class="sign-section" v-if="hasLogin">
				<view class="loading" v-if="pageLoading">
					<view class="loading-spinner"></view>
					<text class="loading-text">加载签到数据中...</text>
				</view>

				<view class="sign-content" v-else>
					<view class="sign-header">
						<text class="sign-title">每日签到</text>
						<view class="sign-streak">
							<text class="streak-icon">🔥</text>
							<text class="streak-text">连续签到 {{continuousDays}} 天</text>
						</view>
					</view>

					<view class="sign-btn loading" v-if="signLoading">
						<view class="btn-spinner"></view>
						<text class="btn-text">签到中...</text>
					</view>
					<view 
						class="sign-btn can-sign" 
						@click="handleSign"
						v-else-if="!todayStatus.signed"
					>
						<text class="btn-text">今日签到 +{{todayReward}}积分</text>
					</view>
					<view class="signed-btn" v-else>
						<text class="signed-icon">✅</text>
						<text class="signed-text">今日已签到</text>
					</view>

					<view class="makeup-area" v-if="makeupDates.length > 0">
						<view class="makeup-header">
							<text class="makeup-title">可补签日期</text>
							<text class="makeup-desc">近3天内有 {{makeupDates.length}} 天未签到，补签需消耗50积分/天</text>
						</view>
						
						<view class="date-selector">
							<picker 
								mode="date" 
								:start="startDate" 
								:end="yesterday" 
								:value="selectedMakeupDate"
								@change="onDateChange"
							>
								<view class="picker-view">
									<text class="picker-icon">📅</text>
									<text>选择补签日期：{{selectedMakeupDate}}</text>
								</view>
							</picker>
						</view>
						
						<view class="makeup-btn-container">
							<button 
								class="makeup-btn" 
								@click="handleMakeup"
								:disabled="makeupLoading"
							>
								<text v-if="!makeupLoading">
									<text class="makeup-icon">🌟</text>确认补签（消耗50积分）
								</text>
								<text v-if="makeupLoading">
									<view class="makeup-spinner"></view>补签中...
								</text>
							</button>
						</view>
					</view>
				</view>
			</view>

			<!-- 未登录提示 -->
			<view class="sign-unlogin" v-else>
				<text class="unlogin-icon">🔒</text>
				<text class="unlogin-text">登录后可参与每日签到</text>
				<button class="login-btn" @click="navTo('/pages/public/login')">
					<text class="login-btn-icon">→</text>立即登录
				</button>
			</view>

			<!-- 订单区域 -->
			<view class="order-section">
				<view class="order-item" @click="navTo('/pages/order/order?state=0')" hover-class="order-item-hover">
					<text class="yticon icon-shouye"></text>
					<text class="order-text">全部订单</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=1')" hover-class="order-item-hover">
					<text class="yticon icon-daifukuan"></text>
					<text class="order-text">待付款</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=2')" hover-class="order-item-hover">
					<text class="yticon icon-yishouhuo"></text>
					<text class="order-text">待收货</text>
				</view>
				<view class="order-item" hover-class="order-item-hover">
					<text class="yticon icon-shouhoutuikuan"></text>
					<text class="order-text">退款/售后</text>
				</view>
			</view>

			<view class="history-section icon">
				<list-cell 
					icon="icon-dizhi" 
					iconColor="#5fcda2" 
					title="地址管理" 
					@eventClick="navTo('/pages/address/address')"
					hover-class="list-cell-hover"
				></list-cell>
				<list-cell 
					icon="icon-lishijilu" 
					iconColor="#e07472" 
					title="我的足迹" 
					@eventClick="navTo('/pages/user/readHistory')"
					hover-class="list-cell-hover"
				></list-cell>
				<list-cell 
					icon="icon-shoucang" 
					iconColor="#5fcda2" 
					title="我的关注" 
					@eventClick="navTo('/pages/user/brandAttention')"
					hover-class="list-cell-hover"
				></list-cell>
				<list-cell 
					icon="icon-shoucang_xuanzhongzhuangtai" 
					iconColor="#54b4ef" 
					title="我的收藏" 
					@eventClick="navTo('/pages/user/productCollection')"
					hover-class="list-cell-hover"
				></list-cell>
				<list-cell 
					icon="icon-pingjia" 
					iconColor="#ee883b" 
					title="我的评价"
					hover-class="list-cell-hover"
				></list-cell>
				<list-cell 
				  icon="icon-pingjia" 
				  iconColor="#ee883b" 
				  title="我的签到记录"
				  @eventClick="navTo('/pages/user/signInRecords/signInRecords')" 
				  hover-class="list-cell-hover"
				></list-cell>
				
				<list-cell 
					icon="icon-shezhi1" 
					iconColor="#e07472" 
					title="设置" 
					border="" 
					@eventClick="navTo('/pages/set/set')"
					hover-class="list-cell-hover"
				></list-cell>
				
	
				
			
			</view>
		</view>

		<!-- 弹窗组件 -->
		<view class="popup-mask" v-if="showSuccessPopup" @click="closePopup">
			<view class="popup-content success" @click.stop>
				<text class="popup-icon">🎉</text>
				<text class="popup-title">签到成功</text>
				<text class="popup-desc">获得 {{rewardInfo.points}} 积分 + {{rewardInfo.growth}} 成长值</text>
				<text class="popup-streak">当前连续签到 {{continuousDays + 1}} 天</text>
				<button class="popup-confirm success-confirm" @click.stop="closePopup">确定</button>
			</view>
		</view>

		<view class="popup-mask" v-if="showMakeupPopup" @click="closePopup">
			<view class="popup-content makeup" @click.stop>
				<text class="popup-icon">🌟</text>
				<text class="popup-title">补签成功</text>
				<text class="popup-desc">已成功补签 {{selectedMakeupDate}}</text>
				<text class="popup-cost">消耗 50 积分，剩余积分：{{(userInfo.integration || 0) - 50}}</text>
				<button class="popup-confirm makeup-confirm" @click.stop="closePopup">确定</button>
			</view>
		</view>

		<view class="popup-mask" v-if="showSystemErrorPopup" @click="closePopup">
			<view class="popup-content system-error" @click.stop>
				<text class="popup-icon">⚠️</text>
				<text class="popup-title">系统提示</text>
				<text class="popup-desc">{{systemErrorMsg}}</text>
				<text class="popup-tip">建议稍后重试或联系客服~</text>
				<button class="popup-confirm error-confirm" @click.stop="closePopup">知道了</button>
			</view>
		</view>

		<view class="custom-error-popup" v-if="showCustomError">
			<view class="error-content" :animation="errorAnimation">
				<text class="error-icon">⚠️</text>
				<text class="error-text">{{customErrorMsg}}</text>
			</view>
		</view>
    </view>  
</template>  

<script>  
import listCell from '@/components/mix-list-cell';
import { fetchMemberCouponList } from '@/api/coupon.js';
import { 
  getTodaySignInStatus,
  doSignIn, 
  doMakeUpSignIn, 
  getSignInRecords, 
  getContinuousSignInDays,
  getMemberPoints // 导入积分成长值查询接口
} from '@/api/signin.js';
import { mapState } from 'vuex';  

const formatDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const getDateFromISO = (isoStr) => isoStr?.split('T')[0] || '';

let startY = 0, moveY = 0, pageAtTop = true;
export default {
  components: { listCell },
  data() {
    return {
      coverTransform: 'translateY(0px)',
      coverTransition: '0s',
      moving: false,
      couponCount: 0,
      pageLoading: true,
      
      todayStatus: { signed: false, makeup: false },
      continuousDays: 0,
      makeupDates: [],
      todayReward: 5,
      
      signLoading: false,
      makeupLoading: false,
      
      selectedMakeupDate: '',
      startDate: '',
      yesterday: '',
      
      showSuccessPopup: false,
      showMakeupPopup: false,
      showSystemErrorPopup: false,
      showCustomError: false,
      systemErrorMsg: '',
      customErrorMsg: '',
      errorAnimation: {},
      rewardInfo: { points: 0, growth: 0 }
    };
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo'])
  },
  onShow() {
    if (this.hasLogin) {
      this.fetchCouponCount();
      this.initSignData();
      this.fetchMemberPoints(); // 加载时查询积分和成长值
    } else {
      this.couponCount = 0;
      this.pageLoading = false;
    }
  },
  onLoad() {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    
    const threeDaysAgo = new Date(today);
    threeDaysAgo.setDate(today.getDate() - 3);
    
    this.startDate = formatDate(threeDaysAgo);
    this.yesterday = formatDate(yesterday);
    this.selectedMakeupDate = this.yesterday;
  },
  methods: {
    // 新增：查询会员积分和成长值
    async fetchMemberPoints() {
      try {
        const res = await getMemberPoints();
        const pointsData = res.data;
        // 更新Vuex中的积分和成长值
        this.$store.commit('updateIntegration', pointsData.totalIntegration);
        this.$store.commit('updateGrowth', pointsData.totalGrowth);
      } catch (err) {
        this.showErrorPopup('查询积分和成长值失败，请稍后重试');
        this.$store.commit('updateIntegration', 0);
        this.$store.commit('updateGrowth', 0);
      }
    },

    async initSignData() {
      this.pageLoading = true;
      try {
        const statusRes = await getTodaySignInStatus();
        const statusData = statusRes.data;
        this.todayStatus = {
          signed: !statusData.success,
          makeup: statusData.makeup || false
        };

        const [daysRes, recordsRes] = await Promise.all([
          getContinuousSignInDays(),
          getSignInRecords(new Date().getFullYear(), new Date().getMonth() + 1)
        ]);
        
        this.continuousDays = daysRes.data || 0;
        this.makeupDates = this.calcMakeupDates(recordsRes.data);
        this.calcTodayReward();

      } catch (err) {
        this.systemErrorMsg = '加载签到数据失败，请稍后重试';
        this.showSystemErrorPopup = true;
      } finally {
        this.pageLoading = false;
      }
    },

    calcMakeupDates(records) {
      const today = new Date();
      const recent3Days = [];
      for (let i = 1; i <= 3; i++) {
        const d = new Date(today);
        d.setDate(today.getDate() - i);
        recent3Days.push(formatDate(d));
      }

      const signedDates = records.map(item => getDateFromISO(item.signInDate));
      return recent3Days.filter(date => !signedDates.includes(date));
    },

    calcTodayReward() {
      if (this.todayStatus.signed) {
        this.todayReward = 0;
        return;
      }
      const rewards = [5, 8, 10, 12, 15, 18, 20];
      const index = this.continuousDays % rewards.length;
      this.todayReward = rewards[index];
    },

    fetchCouponCount() {
      fetchMemberCouponList(0).then(response => {
        this.couponCount = (response.data && response.data.length) ? response.data.length : 0;
      }).catch(() => {
        this.couponCount = 0;
      });
    },

    navTo(url) {
      if (!this.hasLogin && !url.includes('login')) {
        url = '/pages/public/login';
        uni.showToast({ title: '请先登录', icon: 'none' });
      }
      uni.navigateTo({ url });
    },

    coverTouchstart(e) {
      if (pageAtTop === false) return;
      this.coverTransition = 'transform .1s linear';
      startY = e.touches[0].clientY;
    },
    coverTouchmove(e) {
      moveY = e.touches[0].clientY;
      const moveDistance = moveY - startY;
      if (moveDistance < 0) {
        this.moving = false;
        return;
      }
      this.moving = true;
      const limitDistance = moveDistance > 80 ? 80 : moveDistance;
      this.coverTransform = `translateY(${limitDistance}px)`;
    },
    coverTouchend() {
      if (!this.moving) return;
      this.moving = false;
      this.coverTransition = 'transform 0.3s cubic-bezier(.21,1.93,.53,.64)';
      this.coverTransform = 'translateY(0px)';
    },

    async handleSign() {
      if (this.todayStatus.signed || this.signLoading) return;
      this.signLoading = true;

      try {
        const res = await doSignIn();
        const signData = res.data;
        
        this.rewardInfo = {
          points: signData.integration || 0,
          growth: signData.growth || 0
        };
        this.showSuccessPopup = true;
        
        this.todayStatus.signed = true;
        this.continuousDays += 1;
        this.$store.commit('updateIntegration', (this.userInfo.integration || 0) + this.rewardInfo.points);
        this.$store.commit('updateGrowth', (this.userInfo.growth || 0) + this.rewardInfo.growth);

      } catch (err) {
        const errorMsg = err.response?.data?.message || err.message || '签到失败';
        if (errorMsg.includes('已经签到过了')) {
          uni.showToast({ title: '今天已经签到过啦～', icon: 'none', duration: 1500 });
          this.todayStatus.signed = true;
        } else {
          this.systemErrorMsg = errorMsg;
          this.showSystemErrorPopup = true;
        }

      } finally {
        this.signLoading = false;
      }
    },

    onDateChange(e) {
      this.selectedMakeupDate = e.detail.value;
    },

    showErrorPopup(msg) {
      this.customErrorMsg = msg;
      this.showCustomError = true;
      
      this.errorAnimation = uni.createAnimation({
        duration: 300,
        timingFunction: 'ease-out'
      }).opacity(1).scale(1.05).step().scale(1).step().export();
      
      setTimeout(() => {
        this.showCustomError = false;
      }, 3000);
    },

    async handleMakeup() {
      const currentPoints = this.userInfo.integration || 0;
      if (currentPoints < 50) {
        this.showErrorPopup('积分不足50，无法补签哦～');
        return;
      }
      if (this.makeupLoading) return;
      this.makeupLoading = true;

      try {
        const res = await doMakeUpSignIn(this.selectedMakeupDate);
        const makeupData = res.data;
        
        if (makeupData.success) {
          this.showMakeupPopup = true;
          this.makeupDates = this.makeupDates.filter(date => date !== this.selectedMakeupDate);
          this.$store.commit('updateIntegration', currentPoints - 50);
        } else {
          this.showErrorPopup(makeupData.message || '补签失败');
        }

      } catch (err) {
        const errorMsg = err.response?.data?.message || '补签请求失败';
        this.showErrorPopup(errorMsg);
        if (err.response?.data?.currentIntegration !== undefined) {
          this.$store.commit('updateIntegration', err.response.data.currentIntegration);
        }

      } finally {
        this.makeupLoading = false;
      }
    },

    closePopup() {
      this.showSuccessPopup = false;
      this.showMakeupPopup = false;
      this.showSystemErrorPopup = false;
      this.showCustomError = false;
      this.systemErrorMsg = '';
      this.customErrorMsg = '';
    },

    handleAvatarClick() {
      uni.showToast({ title: '查看个人资料', icon: 'none', duration: 1500 });
    }
  }
}
</script>

<style scoped>
	/* 全局样式统一 */
	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}
	.container {
		min-height: 100vh;
		background: #F5F5F5;
		font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
	}

	/* 用户头部区域 */
	.user-section {
		height: 460upx;
		padding: 70upx 30upx 20upx;
		position: relative;
		overflow: hidden;
	}
	.user-section .bg {
		position: absolute;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		object-fit: cover;
		filter: blur(4upx);
	}
	.user-section .user-mask {
		position: absolute;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		background: linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.3));
		z-index: 1;
	}
	.user-info-box {
		height: 150upx;
		display: flex;
		align-items: center;
		position: relative;
		z-index: 2;
		margin-bottom: 20upx;
	}
	.portrait-shadow {
		width: 130upx;
		height: 130upx;
		border-radius: 50%;
		background: linear-gradient(45deg, #FFD700, #FFA500);
		display: flex;
		justify-content: center;
		align-items: center;
		box-shadow: 0 0 30upx rgba(255, 152, 0, 0.5);
		position: relative;
		transition: transform 0.3s ease;
	}
	.portrait-shadow:hover {
		transform: rotate(5deg) scale(1.05);
	}
	.portrait-ring {
		position: absolute;
		width: 140upx;
		height: 140upx;
		border: 2upx dashed rgba(255, 255, 255, 0.7);
		animation: ringRotate 8s linear infinite;
	}
	@keyframes ringRotate {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
	.portrait {
		width: 120upx;
		height: 120upx;
		border-radius: 50%;
		border: 4upx solid #FFFFFF;
		object-fit: cover;
		z-index: 2;
	}
	.info-box {
		margin-left: 24upx;
	}
	.username {
		font-size: 32upx;
		color: #FFFFFF;
		font-weight: 600;
		text-shadow: 0 2upx 8upx rgba(0, 0, 0, 0.3);
		margin-bottom: 8upx;
	}
	.login-tag {
		display: inline-block;
		font-size: 22upx;
		color: #FFFFFF;
		background: #4CAF50;
		padding: 4upx 16upx;
		border-radius: 20upx;
		box-shadow: 0 2upx 4upx rgba(0, 0, 0, 0.2);
	}
	.login-tag.unlogin {
		background: #999999;
	}

	/* VIP卡片 */
	.vip-card-box {
		display: flex;
		flex-direction: column;
		color: #F7D680;
		height: 200upx;
		background: linear-gradient(135deg, rgba(0,0,0,0.8), rgba(0,0,0,0.95));
		border-radius: 16upx;
		overflow: hidden;
		position: relative;
		padding: 20upx 24upx;
		z-index: 2;
		box-shadow: 0 6upx 20upx rgba(0, 0, 0, 0.3);
		border: 1upx solid rgba(255, 215, 0, 0.2);
		transition: transform 0.3s ease;
	}
	.vip-card-box:hover {
		transform: translateY(-3upx);
	}
	.vip-card-box .card-bg {
		position: absolute;
		top: 0;
		right: 0;
		width: 380upx;
		height: 100%;
		object-fit: cover;
		opacity: 0.2;
	}
	.card-decor {
		position: absolute;
		top: 15upx;
		left: 15upx;
		width: 50upx;
		height: 50upx;
		background: rgba(255, 215, 0, 0.3);
		border-radius: 50%;
		filter: blur(8upx);
		z-index: 1;
	}
	.b-btn {
		position: absolute;
		right: 24upx;
		top: 20upx;
		width: 140upx;
		height: 44upx;
		text-align: center;
		line-height: 44upx;
		font-size: 22upx;
		color: #36343C;
		border-radius: 22upx;
		background: linear-gradient(90deg, #FFE9B4, #FFCC4D);
		z-index: 2;
		font-weight: 600;
		box-shadow: 0 3upx 10upx rgba(255, 212, 101, 0.5);
		transition: all 0.2s ease;
	}
	.b-btn-hover {
		transform: scale(1.05);
		background: linear-gradient(90deg, #FFF3CC, #FFD54F);
		box-shadow: 0 5upx 14upx rgba(255, 212, 101, 0.7);
	}
	.tit {
		font-size: 28upx;
		color: #F7D680;
		margin-bottom: 18upx;
		z-index: 2;
		font-weight: 500;
	}
	.tit .yticon {
		color: #FFE9B4;
		margin-right: 12upx;
		font-size: 30upx;
	}
	.e-m {
		font-size: 22upx;
		color: #F7D680;
		margin-bottom: 8upx;
		z-index: 2;
	}
	.e-b {
		font-size: 20upx;
		color: #D8CBA9;
		z-index: 2;
		opacity: 0.9;
		line-height: 30upx;
	}

	/* 内容容器 */
	.cover-container {
		background: #F5F5F5;
		margin-top: -80upx;
		padding: 0 30upx;
		position: relative;
		padding-bottom: 60upx;
	}
	.arc {
		position: absolute;
		left: 0;
		top: -34upx;
		width: 100%;
		height: 36upx;
		object-fit: cover;
		z-index: 1;
		opacity: 0.9;
	}

	/* 积分成长值区域 */
	.tj-sction {
		display: flex;
		justify-content: space-around;
		align-items: center;
		background: #FFFFFF;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		margin-top: 25upx;
		padding: 32upx 0;
		transition: box-shadow 0.3s ease;
		position: relative;
		z-index: 2;
	}
	.tj-sction:hover {
		box-shadow: 0 8upx 24upx rgba(0, 0, 0, 0.1);
	}
	.tj-item {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		width: 120upx;
		transition: all 0.2s ease;
		padding: 16upx 0;
		border-radius: 12upx;
	}
	.tj-item-hover {
		background: rgba(255, 152, 0, 0.05);
		transform: translateY(-4upx);
	}
	.tj-icon {
		font-size: 42upx;
		margin-bottom: 10upx;
		color: #FF9800;
		transition: transform 0.3s ease;
	}
	.tj-item-hover .tj-icon {
		transform: scale(1.15);
	}
	.num {
		font-size: 38upx;
		color: #333333;
		margin-bottom: 6upx;
		font-weight: 600;
	}
	.label {
		font-size: 24upx;
		color: #666666;
	}

	/* 签到区域 */
	.sign-section {
		background: #FFFFFF;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		margin-top: 24upx;
		padding: 32upx;
		transition: box-shadow 0.3s ease;
	}
	.sign-section:hover {
		box-shadow: 0 8upx 24upx rgba(0, 0, 0, 0.1);
	}
	.loading {
		text-align: center;
		padding: 60upx 0;
	}
	.loading-spinner {
		width: 48upx;
		height: 48upx;
		border: 4upx solid rgba(255, 152, 0, 0.2);
		border-top-color: #FF9800;
		border-radius: 50%;
		margin: 0 auto 20upx;
		animation: spin 1s linear infinite;
	}
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
	.loading-text {
		font-size: 26upx;
		color: #999999;
	}
	.sign-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 28upx;
	}
	.sign-title {
		font-size: 32upx;
		color: #333333;
		font-weight: 600;
	}
	.sign-streak {
		display: flex;
		align-items: center;
		font-size: 24upx;
		color: #FF9800;
		background: rgba(255, 152, 0, 0.1);
		padding: 6upx 16upx;
		border-radius: 20upx;
	}
	.streak-icon {
		margin-right: 10upx;
		font-size: 28upx;
	}
	.sign-btn {
		width: 100%;
		height: 96upx;
		line-height: 96upx;
		text-align: center;
		border-radius: 16upx;
		margin: 10upx 0 28upx;
		font-size: 32upx;
		font-weight: 600;
		transition: all 0.2s ease;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.sign-btn.can-sign {
		background: linear-gradient(90deg, #FF9800, #FF7A45);
		color: #FFFFFF;
		box-shadow: 0 6upx 16upx rgba(255, 122, 69, 0.3);
	}
	.sign-btn.can-sign:active {
		transform: scale(0.98);
		opacity: 0.95;
		box-shadow: 0 4upx 12upx rgba(255, 122, 69, 0.2);
	}
	.sign-btn.loading {
		background: #F5F5F5;
		color: #999999;
		cursor: not-allowed;
	}
	.btn-spinner {
		width: 32upx;
		height: 32upx;
		border: 2upx solid rgba(153, 153, 153, 0.2);
		border-top-color: #999999;
		border-radius: 50%;
		margin-right: 12upx;
		animation: spin 1s linear infinite;
	}
	.signed-btn {
		width: 100%;
		height: 96upx;
		line-height: 96upx;
		text-align: center;
		border-radius: 16upx;
		margin: 10upx 0 28upx;
		font-size: 32upx;
		background: rgba(76, 175, 80, 0.1);
		color: #4CAF50;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.signed-icon {
		margin-right: 12upx;
		font-size: 36upx;
	}
	.makeup-area {
		background: rgba(255, 152, 0, 0.05);
		border-radius: 16upx;
		padding: 24upx;
		margin-top: 10upx;
		border: 1upx solid rgba(255, 152, 0, 0.1);
	}
	.makeup-header {
		margin-bottom: 24upx;
	}
	.makeup-title {
		font-size: 28upx;
		color: #FF9800;
		font-weight: 600;
		margin-bottom: 10upx;
		display: block;
	}
	.makeup-desc {
		font-size: 24upx;
		color: #666666;
		line-height: 36upx;
	}
	.date-selector {
		background: #FFFFFF;
		border-radius: 12upx;
		padding: 20upx 24upx;
		margin-bottom: 24upx;
		box-shadow: 0 2upx 8upx rgba(0, 0, 0, 0.05);
	}
	.picker-view {
		font-size: 26upx;
		color: #333333;
		display: flex;
		align-items: center;
	}
	.picker-icon {
		margin-right: 12upx;
		color: #FF9800;
	}
	.makeup-btn-container {
		width: 100%;
	}
	.makeup-btn {
		width: 100%;
		height: 88upx;
		line-height: 88upx;
		background: linear-gradient(90deg, #FFB74D, #FF9800);
		color: #FFFFFF;
		border-radius: 12upx;
		font-size: 28upx;
		border: none;
		font-weight: 500;
		transition: all 0.2s ease;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.makeup-btn:disabled {
		background: #FFCC80;
		color: #FFFFFF;
		cursor: not-allowed;
	}
	.makeup-btn:active {
		transform: scale(0.98);
		box-shadow: 0 4upx 12upx rgba(255, 152, 0, 0.2);
	}
	.makeup-icon {
		margin-right: 12upx;
	}
	.makeup-spinner {
		width: 28upx;
		height: 28upx;
		border: 2upx solid rgba(255, 255, 255, 0.3);
		border-top-color: #FFFFFF;
		border-radius: 50%;
		margin-right: 12upx;
		animation: spin 1s linear infinite;
	}

	/* 未登录提示 */
	.sign-unlogin {
		background: #FFFFFF;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		margin-top: 24upx;
		padding: 60upx 20upx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}
	.unlogin-icon {
		font-size: 64upx;
		margin-bottom: 28upx;
		color: #999999;
	}
	.unlogin-text {
		font-size: 28upx;
		color: #666666;
		margin-bottom: 32upx;
		text-align: center;
	}
	.login-btn {
		width: 240upx;
		height: 64upx;
		line-height: 64upx;
		background: linear-gradient(90deg, #FF9800, #FF7A45);
		color: #FFFFFF;
		border-radius: 32upx;
		font-size: 26upx;
		border: none;
		font-weight: 500;
		transition: all 0.2s ease;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.login-btn:active {
		transform: scale(0.98);
		box-shadow: 0 4upx 12upx rgba(255, 122, 69, 0.2);
	}
	.login-btn-icon {
		margin-right: 8upx;
		font-size: 28upx;
	}

	/* 订单区域 */
	.order-section {
		background: #FFFFFF;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		margin-top: 24upx;
		display: flex;
		justify-content: space-around;
		align-items: center;
		padding: 36upx 0;
		transition: box-shadow 0.3s ease;
	}
	.order-section:hover {
		box-shadow: 0 8upx 24upx rgba(0, 0, 0, 0.1);
	}
	.order-item {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		width: 120upx;
		height: 140upx;
		border-radius: 12upx;
		transition: all 0.2s ease;
		padding: 16upx 0;
	}
	.order-item-hover {
		background: rgba(255, 122, 69, 0.05);
		transform: translateY(-6upx);
		box-shadow: 0 6upx 12upx rgba(0, 0, 0, 0.05);
	}
	.order-item .yticon {
		font-size: 52upx;
		margin-bottom: 20upx;
		color: #FF7A45;
		transition: color 0.2s ease;
	}
	.order-item-hover .yticon {
		color: #FF9800;
	}
	.order-text {
		font-size: 24upx;
		color: #333333;
		font-weight: 500;
	}

	/* 功能列表 */
	.history-section {
		background: #FFFFFF;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		margin-top: 24upx;
		overflow: hidden;
		transition: box-shadow 0.3s ease;
	}
	.history-section:hover {
		box-shadow: 0 8upx 24upx rgba(0, 0, 0, 0.1);
	}
	.list-cell {
		padding: 28upx 30upx;
		border-bottom: 1upx solid rgba(0, 0, 0, 0.05);
		display: flex;
		align-items: center;
		cursor: pointer;
		transition: all 0.2s ease;
		position: relative;
	}
	.list-cell::after {
		content: '→';
		position: absolute;
		right: 30upx;
		font-size: 24upx;
		color: #999999;
		transition: color 0.2s ease;
	}
	.list-cell:last-child {
		border-bottom: none;
	}
	.list-cell .yticon {
		font-size: 36upx;
		margin-right: 24upx;
	}
	.cell-title {
		font-size: 28upx;
		color: #333333;
		flex: 1;
		font-weight: 500;
	}
	.list-cell-hover {
		background: rgba(255, 122, 69, 0.03);
	}
	.list-cell-hover::after {
		color: #FF9800;
	}

	/* 弹窗样式 */
	.popup-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 999;
		backdrop-filter: blur(4upx);
	}
	.popup-content {
		background: #FFFFFF;
		border-radius: 24upx;
		padding: 48upx;
		width: 85%;
		max-width: 500upx;
		text-align: center;
		box-shadow: 0 10upx 40upx rgba(0, 0, 0, 0.2);
		transform: scale(0.95);
		animation: popupShow 0.3s ease forwards;
	}
	@keyframes popupShow {
		100% { transform: scale(1); }
	}
	.popup-content.success {
		border: 1upx solid rgba(76, 175, 80, 0.2);
	}
	.popup-content.makeup {
		border: 1upx solid rgba(255, 152, 0, 0.2);
	}
	.popup-content.system-error {
		background: #FFFDF5;
		border: 1upx solid #FFF0CC;
	}
	.popup-icon {
		font-size: 72upx;
		display: block;
		margin-bottom: 28upx;
	}
	.popup-content.success .popup-icon {
		color: #4CAF50;
	}
	.popup-content.makeup .popup-icon {
		color: #FF9800;
	}
	.popup-content.system-error .popup-icon {
		color: #FFC107;
	}
	.popup-title {
		font-size: 34upx;
		font-weight: 600;
		color: #333333;
		margin-bottom: 24upx;
	}
	.popup-content.system-error .popup-title {
		color: #FF8C00;
	}
	.popup-desc {
		font-size: 26upx;
		color: #666666;
		margin-bottom: 20upx;
		line-height: 44upx;
	}
	.popup-tip {
		font-size: 22upx;
		color: #999999;
		margin-bottom: 36upx;
	}
	.popup-streak, .popup-cost {
		font-size: 24upx;
		color: #999999;
		margin-bottom: 36upx;
	}
	.popup-confirm {
		width: 100%;
		height: 88upx;
		line-height: 88upx;
		border-radius: 16upx;
		font-size: 28upx;
		border: none;
		font-weight: 500;
		transition: all 0.2s ease;
	}
	.success-confirm {
		background: #4CAF50;
		color: #FFFFFF;
	}
	.makeup-confirm {
		background: #FF9800;
		color: #FFFFFF;
	}
	.error-confirm {
		background: #FF9800;
		color: #FFFFFF;
	}
	.popup-confirm:active {
		transform: scale(0.98);
		opacity: 0.95;
	}

	/* 错误提示弹窗 */
	.custom-error-popup {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		justify-content: center;
		align-items: flex-start;
		padding-top: 200upx;
		background: transparent;
		z-index: 999;
		pointer-events: none;
	}
	.error-content {
		display: flex;
		align-items: center;
		gap: 16upx;
		padding: 24upx 40upx;
		background: linear-gradient(135deg, #FFF8F0, #FFF0E0);
		border-radius: 16upx;
		box-shadow: 0 8upx 24upx rgba(255, 122, 69, 0.15);
		border: 1upx solid #FFE8CC;
		opacity: 0;
		transform: scale(0.9);
		pointer-events: auto;
	}
	.error-icon {
		font-size: 40upx;
		color: #FF9800;
	}
	.error-text {
		font-size: 26upx;
		color: #E67E22;
		line-height: 36upx;
		max-width: 500upx;
		text-align: center;
	}
</style>