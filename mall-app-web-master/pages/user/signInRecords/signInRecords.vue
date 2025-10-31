<template>
  <view class="sign-record-container">
    <!-- 标题栏 -->
    <view class="page-header">
      <text class="header-title">我的签到记录</text>
    </view>

    <!-- 加载状态 -->
    <view class="loading" v-if="pageLoading">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载签到记录中...</text>
    </view>

    <!-- 加载失败状态 -->
    <view class="error-container" v-else-if="errorMsg">
      <text class="error-icon">⚠️</text>
      <text class="error-text">{{ errorMsg }}</text>
      <button class="retry-btn" @click="fetchSignInRecords">重新加载</button>
    </view>

    <!-- 空数据提示 -->
    <view class="empty-container" v-else-if="signRecords.length === 0">
      <text class="empty-icon">📅</text>
      <text class="empty-text">暂无签到记录</text>
    </view>

    <!-- 签到记录列表（此时signRecords已确保是数组，可正常循环） -->
    <view class="record-list" v-else>
      <view class="record-item" v-for="item in signRecords" :key="item.id">
        <view class="record-header">
          <text class="record-date">{{ formatDate(item.signInDate) }}</text>
          <text class="record-type">{{ getSignTypeText(item.signInType) }}</text>
        </view>
        <view class="record-reward">
          <text class="reward-item">获得积分：{{ item.integration }}</text>
          <text class="reward-item">获得成长值：{{ item.growth }}</text>
        </view>
        <view class="record-total">
          <text class="total-text">累计积分：{{ item.totalIntegration }} | 累计成长值：{{ item.totalGrowth }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState } from 'vuex';
import { getMemberSignInRecords } from '@/api/signin.js';

export default {
  data() {
    return {
      pageLoading: true,
      signRecords: [], // 始终保持数组格式
      errorMsg: ''
    };
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo'])
  },
  onLoad() {
    if (!this.hasLogin) {
      uni.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/public/login' });
      }, 1500);
      return;
    }
    this.fetchSignInRecords();
  },
  methods: {
    async fetchSignInRecords() {
      this.pageLoading = true;
      this.errorMsg = '';
      try {
        const res = await getMemberSignInRecords();
        // 核心修改：适配后端“单对象”或“数组”格式，统一转为数组
        this.signRecords = Array.isArray(res.data) ? res.data : (res.data ? [res.data] : []);
      } catch (err) {
        this.errorMsg = err.response?.data?.message || '加载签到记录失败，请稍后重试';
        uni.showToast({ title: this.errorMsg, icon: 'none', duration: 2000 });
        this.signRecords = [];
      } finally {
        this.pageLoading = false;
      }
    },
    formatDate(isoDate) {
      return isoDate?.split('T')[0] || '';
    },
    getSignTypeText(type) {
      const typeMap = {
        0: '正常签到',
        1: '补签'
      };
      return typeMap[type] || '未知类型';
    }
  }
};
</script>

<style scoped>
/* 样式与之前完全一致，无需修改 */
.sign-record-container {
  min-height: 100vh;
  background: #F5F5F5;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  padding-bottom: 60upx;
}
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
  background: linear-gradient(90deg, #FF9800, #FF7A45);
  color: #FFFFFF;
  border-radius: 32upx;
  font-size: 24upx;
  border: none;
  box-shadow: 0 4upx 12upx rgba(255, 122, 69, 0.2);
}
.retry-btn:active {
  transform: scale(0.98);
  box-shadow: 0 2upx 8upx rgba(255, 122, 69, 0.15);
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
  color: #FFCC80;
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
  color: #FF9800;
  background: rgba(255, 152, 0, 0.1);
  padding: 4upx 12upx;
  border-radius: 16upx;
}
.record-reward {
  display: flex;
  gap: 30upx;
  margin-bottom: 16upx;
}
.reward-item {
  font-size: 24upx;
  color: #666666;
}
.reward-item::before {
  content: "💎";
  margin-right: 8upx;
  color: #FF9800;
}
.record-total {
  font-size: 22upx;
  color: #999999;
}
.total-text {
  display: block;
  line-height: 32upx;
}
</style>