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

			<!-- 签到入口（改造后） -->
			<view class="sign-entry-section" v-if="hasLogin">
				<view class="entry-card">
					<view class="entry-header">
						<text class="entry-title">每日签到</text>
						<text class="entry-desc">已连续签到 {{continuousDays}} 天</text>
					</view>
					<!-- 点击跳转到签到记录页面 -->
					<view class="goto-sign-btn" @click="navTo('/pages/user/sigin')">
						<text class="btn-text">前往签到</text>
						<text class="btn-icon">→</text>
					</view>
					<view class="entry-footer">
						<text class="footer-text">签到可获积分，补签功能在记录页</text>
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
				  title="积分历史"
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

		<!-- 错误提示弹窗 -->
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
  getContinuousSignInDays,
  getMemberPoints
} from '@/api/signin.js';
import { mapState } from 'vuex';  

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
      continuousDays: 0, // 连续签到天数
      showCustomError: false,
      customErrorMsg: '',
      errorAnimation: {}
    };
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo'])
  },
  onShow() {
    if (this.hasLogin) {
      this.fetchCouponCount();
      this.initSignData();
      this.fetchMemberPoints();
    } else {
      this.couponCount = 0;
      this.pageLoading = false;
    }
  },
  methods: {
    // 查询会员积分和成长值
    async fetchMemberPoints() {
      try {
        const res = await getMemberPoints();
        const pointsData = res.data;
        this.$store.commit('updateIntegration', pointsData.integration);
        this.$store.commit('updateGrowth', pointsData.growth);
      } catch (err) {
        this.showErrorPopup('查询积分失败，请稍后重试');
      }
    },

    // 初始化签到数据（仅获取连续天数）
    async initSignData() {
      this.pageLoading = true;
      try {
        const daysRes = await getContinuousSignInDays();
        this.continuousDays = daysRes.data || 0;
      } catch (err) {
        this.showErrorPopup('加载签到数据失败');
      } finally {
        this.pageLoading = false;
      }
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

	/* 签到入口样式 */
	.sign-entry-section {
		background: #FFFFFF;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		margin-top: 24upx;
		padding: 32upx;
		transition: box-shadow 0.3s ease;
	}
	.sign-entry-section:hover {
		box-shadow: 0 8upx 24upx rgba(0, 0, 0, 0.1);
	}
	.entry-card {
		display: flex;
		flex-direction: column;
		gap: 24upx;
	}
	.entry-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	.entry-title {
		font-size: 32upx;
		color: #333333;
		font-weight: 600;
	}
	.entry-desc {
		font-size: 24upx;
		color: #FF9800;
		background: rgba(255, 152, 0, 0.1);
		padding: 6upx 16upx;
		border-radius: 20upx;
	}
	.goto-sign-btn {
		width: 100%;
		height: 96upx;
		line-height: 96upx;
		background: linear-gradient(90deg, #FF9800, #FF7A45);
		color: #FFFFFF;
		border-radius: 16upx;
		font-size: 32upx;
		font-weight: 600;
		display: flex;
		justify-content: center;
		align-items: center;
		gap: 12upx;
		transition: all 0.2s ease;
	}
	.goto-sign-btn:active {
		transform: scale(0.98);
		opacity: 0.95;
		box-shadow: 0 4upx 12upx rgba(255, 122, 69, 0.2);
	}
	.entry-footer {
		text-align: center;
	}
	.footer-text {
		font-size: 22upx;
		color: #999999;
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