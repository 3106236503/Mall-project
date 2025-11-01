<template>
	<view class="point-record-container">
		<!-- 标题栏 -->
		<view class="page-header">
			<text class="header-title">我的积分改变记录</text>
		</view>

		<!-- 加载状态（初始加载+下拉刷新复用） -->
		<view class="loading" v-if="pageLoading && pointRecords.length === 0">
			<view class="loading-spinner"></view>
			<text class="loading-text">加载积分记录中...</text>
		</view>

		<!-- 加载失败状态 -->
		<view class="error-container" v-else-if="errorMsg">
			<text class="error-icon">⚠️</text>
			<text class="error-text">{{ errorMsg }}</text>
			<button class="retry-btn" @click="fetchPointRecords">重新加载</button>
		</view>

		<!-- 空数据提示 -->
		<view class="empty-container" v-else-if="pointRecords.length === 0">
			<text class="empty-icon">📊</text>
			<text class="empty-text">暂无积分变动记录</text>
		</view>

		<!-- 积分记录列表 -->
		<view class="record-list" v-else>
			<view class="record-item" v-for="item in pointRecords" :key="item.id">
				<!-- 头部：变动时间 + 积分来源 -->
				<view class="record-header">
					<text class="record-date">{{ formatDate(item.createTime) }}</text>
					<text class="record-type">{{ getPointSourceText(item.sourceType) }}</text>
				</view>

				<!-- 核心：变动方向 + 积分数量 + 备注 -->
				<view class="record-core">
					<text class="change-direction" :class="item.changeType === 0 ? 'text-increase' : 'text-decrease'">
						{{ item.changeType === 0 ? '积分增加' : '积分减少' }}
					</text>
					<text class="change-count">
						{{ item.changeType === 0 ? '+' : '-' }}{{ item.changeCount }} 积分
					</text>
					<text class="operate-note">备注：{{ item.operateNote || '无' }}</text>
				</view>

				<!-- 操作人 -->
				<view class="record-operator">
					<text class="operator-text">操作人：{{ item.operateMan || '系统' }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import {
		getMemberPointRecords
	} from '@/api/member.js';

	export default {
		// 1. 页面配置：启用下拉刷新（关键！否则下拉不触发）
		onPullDownRefresh() {
			console.log('触发下拉刷新，重新加载积分记录');
			// 下拉时调用加载方法，刷新数据
			this.fetchPointRecords(true);
		},

		data() {
			return {
				pageLoading: true, // 初始加载状态（下拉刷新时也会复用）
				pointRecords: [], // 存储全量积分记录
				errorMsg: '' // 错误提示
			};
		},

		computed: {
			...mapState(['hasLogin', 'userInfo'])
		},

		onLoad() {
			if (!this.hasLogin) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/public/login'
					});
				}, 1500);
				return;
			}
			this.fetchPointRecords();
		},

		methods: {
			/**
			 * 加载积分记录（新增refresh参数，区分下拉刷新/普通加载）
			 * @param {boolean} isRefresh - 是否为下拉刷新（true=下拉刷新，false=普通加载）
			 */
			async fetchPointRecords(isRefresh = false) {
				// 下拉刷新时，不显示初始加载的loading视图（用uni原生刷新动画）
				if (!isRefresh) this.pageLoading = true;
				this.errorMsg = '';

				try {
					const res = await getMemberPointRecords();
					console.log('CommonResult对象：', res);
					console.log('积分数组：', res.data);

					// 下拉刷新时直接替换数据，普通加载时保留加载状态逻辑
					this.pointRecords = res.data || [];

					console.log('pointRecords是否为数组：', Array.isArray(this.pointRecords));
					console.log('pointRecords长度：', this.pointRecords.length);

				} catch (err) {
					this.errorMsg = err.response?.data?.message || '加载积分记录失败，请稍后重试';
					uni.showToast({
						title: this.errorMsg,
						icon: 'none',
						duration: 2000
					});
					this.pointRecords = [];

				} finally {
					// 2. 关键：下拉刷新完成后，停止原生刷新动画（必须调用）
					if (isRefresh) {
						uni.stopPullDownRefresh();
						// 下拉刷新失败时，提示用户
						if (this.errorMsg) {
							uni.showToast({
								title: '刷新失败，点击重试',
								icon: 'none',
								duration: 1500
							});
						}
					}
					this.pageLoading = false; // 关闭初始加载状态
				}
			},

			formatDate(isoDate) {
				if (!isoDate || isoDate === 'null') return '';

				const date = new Date(isoDate);
				// 取本地时区的年/月/日/时/分/秒（自动转换时区）
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份0-11，需+1
				const day = String(date.getDate()).padStart(2, '0');
				const hours = String(date.getHours()).padStart(2, '0'); // 本地小时（东八区自动+8）
				const minutes = String(date.getMinutes()).padStart(2, '0');
				const seconds = String(date.getSeconds()).padStart(2, '0');

				return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
			},

			// 积分来源映射（不变）
			getPointSourceText(sourceType) {
				const sourceMap = {
					0: '购物获得',
					1: '管理员调整',
					2: '积分充值',
					3: '每日签到',
					4: '补签减少'
				};
				return sourceMap[sourceType] || `未知来源(${sourceType})`;
			}
		}
	};
</script>

<style scoped>
	/* 原有样式不变，新增下拉刷新相关样式（可选，优化体验） */
	.point-record-container {
		min-height: 100vh;
		background: #F5F5F5;
		font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
		padding-bottom: 80upx;
	}

	/* 以下为原有样式，保持不变... */
	.page-header {
		height: 90upx;
		line-height: 90upx;
		padding: 0 30upx;
		background: #FFFFFF;
		border-bottom: 1upx solid rgba(0, 0, 0, 0.05);
		margin-bottom: 24upx;
	}

	.header-title {
		font-size: 32upx;
		color: #333333;
		font-weight: 600;
	}

	.loading {
		text-align: center;
		padding: 80upx 0;
		background: #FFFFFF;
		margin: 0 30upx;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
	}

	.loading-spinner {
		width: 48upx;
		height: 48upx;
		border: 4upx solid rgba(64, 158, 255, 0.2);
		border-top-color: #409EFF;
		border-radius: 50%;
		margin: 0 auto 20upx;
		animation: spin 1s linear infinite;
	}

	@keyframes spin {
		0% {
			transform: rotate(0deg);
		}

		100% {
			transform: rotate(360deg);
		}
	}

	.loading-text {
		font-size: 26upx;
		color: #999999;
	}

	.error-container {
		text-align: center;
		padding: 80upx 0;
		background: #FFFFFF;
		margin: 0 30upx;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
	}

	.error-icon {
		font-size: 72upx;
		color: #FF7A45;
		margin-bottom: 24upx;
		display: block;
	}

	.error-text {
		font-size: 26upx;
		color: #FF7A45;
		margin-bottom: 24upx;
	}

	.retry-btn {
		width: 200upx;
		height: 64upx;
		line-height: 64upx;
		background: linear-gradient(90deg, #409EFF, #66B1FF);
		color: #FFFFFF;
		border-radius: 32upx;
		font-size: 24upx;
		border: none;
		box-shadow: 0 4upx 12upx rgba(64, 158, 255, 0.2);
	}

	.retry-btn:active {
		transform: scale(0.98);
		box-shadow: 0 2upx 8upx rgba(64, 158, 255, 0.15);
	}

	.empty-container {
		text-align: center;
		padding: 80upx 0;
		background: #FFFFFF;
		margin: 0 30upx;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
	}

	.empty-icon {
		font-size: 72upx;
		color: #409EFF;
		margin-bottom: 24upx;
		display: block;
	}

	.empty-text {
		font-size: 26upx;
		color: #999999;
	}

	.record-list {
		background: #FFFFFF;
		margin: 0 30upx;
		border-radius: 16upx;
		box-shadow: 0 4upx 16upx rgba(0, 0, 0, 0.08);
		overflow: hidden;
	}

	.record-item {
		padding: 28upx 30upx;
		border-bottom: 1upx solid rgba(0, 0, 0, 0.05);
	}

	.record-item:last-child {
		border-bottom: none;
	}

	.record-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 16upx;
	}

	.record-date {
		font-size: 28upx;
		color: #333333;
		font-weight: 500;
	}

	.record-type {
		font-size: 22upx;
		color: #409EFF;
		background: rgba(64, 158, 255, 0.1);
		padding: 4upx 12upx;
		border-radius: 16upx;
	}

	.record-core {
		display: flex;
		flex-direction: column;
		gap: 12upx;
		margin-bottom: 16upx;
	}

	.change-direction {
		font-size: 24upx;
		font-weight: 500;
	}

	.text-increase {
		color: #67C23A;
	}

	.text-decrease {
		color: #F56C6C;
	}

	.change-count {
		font-size: 26upx;
		color: #333333;
		font-weight: 500;
	}

	.operate-note {
		font-size: 22upx;
		color: #666666;
	}

	.record-operator {
		font-size: 20upx;
		color: #999999;
	}
</style>