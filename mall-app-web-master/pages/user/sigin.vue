<template>
  <!-- 根容器：渐变背景+全局样式 -->
  <view class="sign-page-container">
    <!-- 1. 顶部标题区：带返回动画 -->
    <view class="page-header">
      <view class="back-btn" @click="navBack" @touchstart="backBtnTouch">
        <text class="back-icon">←</text>
        <text class="back-text">个人中心</text>
      </view>
      <view class="header-title-container">
        <text class="header-title">每日签到</text>
        <text class="header-subtitle" :style="{color: getSignStreakColor()}">
          {{ continuousDays > 0 ? `已连续签到${continuousDays}天` : '开启你的签到之旅吧'}}
        </text>
      </view>
      <view class="empty-btn"></view> <!-- 占位，保持标题居中 -->
    </view>

    <!-- 2. 核心数据区：立体胶囊卡片+数字滚动动画 -->
    <view class="data-section">
      <view class="data-card" :class="{'data-card--glow': continuousDays >= 7}">
        <!-- 累计签到 -->
        <view class="data-item">
          <view class="data-label">
            <text>累计签到</text>
            <text class="streak-badge" v-if="continuousDays >= 7">
              {{ continuousDays >= 30 ? '🏆' : continuousDays >= 14 ? '🌟' : '🔥' }}
            </text>
          </view>
          <view class="data-value" :number-animation="totalSignDays">
            {{ totalSignDays }}
            <text class="data-unit">天</text>
          </view>
        </view>
        <!-- 分割线 -->
        <view class="data-divider"></view>
        <!-- 累计积分 -->
        <view class="data-item">
          <view class="data-label">
            <text>累计积分</text>
          </view>
          <view class="data-value" :number-animation="userInfo.integration || 0">
            {{ userInfo.integration || 0 }}
            <text class="data-unit">分</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 3. 立即签到按钮：特色动效+交互反馈 -->
    <view class="sign-now-section" v-if="isCurrentMonthPage && !isTodaySigned && !signLoading">
      <button class="sign-now-btn" @click="handleSignToday" @touchstart="signBtnTouch">
        <text class="sign-icon">✨</text>
        <text class="sign-text">立即签到 +{{ todayReward || 5 }}积分</text>
        <text class="sign-loading" v-if="signLoading">⟳</text>
      </button>
      <view class="sign-tip">
        <text>今日签到可获</text>
        <text class="tip-highlight">{{ todayReward || 5 }}积分</text>
        <text v-if="continuousDays >= 3">（连续签到加成）</text>
      </view>
    </view>

    <!-- 4. 签到成功庆祝动画（全屏） -->
    <view class="success-celebration" v-if="showCelebration">
      <view class="celebration-icon">🎉</view>
      <view class="celebration-title">签到成功！</view>
      <view class="celebration-desc">
        获得{{ lastSignIntegration || 0 }}积分+{{ lastSignGrowth || 0 }}成长值
      </view>
      <!-- 掉落积分图标 -->
      <view class="score-fall" v-for="(item, idx) in 10" :key="idx" :style="{
        left: `${Math.random() * 80 + 10}%`,
        animationDelay: `${idx * 0.1}s`,
        animationDuration: `${Math.random() * 1 + 1.5}s`
      }">
        💎
      </view>
      <button class="celebration-btn" @click="closeCelebration">
        继续探索
      </button>
    </view>

    <!-- 5. 日历区域：特色样式+交互动效 -->
    <view class="calendar-section">
      <!-- 月份切换栏 -->
      <view class="calendar-header">
        <button class="month-btn" @click="changeMonth(-1)" :disabled="isPrevMonthDisabled">
          ←
        </button>
        <view class="month-title">
          {{ currentYear }}年{{ currentMonth }}月
        </view>
        <button class="month-btn" @click="changeMonth(1)">
          →
        </button>
      </view>

      <!-- 星期标题 -->
      <view class="week-header">
        <view class="week-day" v-for="day in ['日','一','二','三','四','五','六']" :key="day">
          {{ day }}
        </view>
      </view>

      <!-- 日期网格：特色卡片+状态动效 -->
      <view class="date-grid">
        <view 
          class="date-card" 
          v-for="(date, idx) in dateList"
          :key="idx"
          @click="handleDateClick(date)"
          :class="{
            'date-card--empty': !date.isCurrentMonth,
            'date-card--signed': date.isSigned,
            'date-card--today': date.isToday,
            'date-card--makeup': date.canMakeup && !date.isSigned,
            'date-card--selected': date.date === selectedMakeupDate
          }"
          @touchstart="dateCardTouch(idx)"
        >
          <!-- 日期数字 -->
          <view class="date-num">{{ date.day }}</view>
          
          <!-- 状态标签组 -->
          <view class="date-tags">
            <view class="tag tag--signed" v-if="date.isSigned">
              <text class="tag-icon">✓</text>
              <text class="tag-text">已签</text>
            </view>
            <view class="tag tag--makeup" v-if="date.canMakeup && !date.isSigned">
              <text class="tag-icon">🔄</text>
              <text class="tag-text">可补</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 6. 补签区域：动态交互+积分提示 -->
    <view class="makeup-section" v-if="hasMakeupDate">
      <view class="makeup-header">
        <view class="makeup-title">
          <text class="title-icon">🔄</text>
          <text>补签操作</text>
        </view>
        <view class="makeup-desc">
          选中可补签日期，消耗50积分补签（当前积分：
          <text class="desc-highlight">{{ userInfo.integration || 0 }}分</text>）
        </view>
      </view>

      <!-- 选中补签日期提示 -->
      <view class="makeup-selected" v-if="selectedMakeupDate">
        <text class="selected-icon">📍</text>
        <text>已选中：{{ selectedMakeupDate }}</text>
        <view class="makeup-cost" v-if="(userInfo.integration || 0) >= 50">
          <text class="cost-icon">💎</text>
          <text>将消耗50积分</text>
        </view>
      </view>

      <!-- 补签按钮：特色动效 -->
      <button 
        class="makeup-btn" 
        @click="handleMakeup"
        :disabled="!selectedMakeupDate || makeupLoading || (userInfo.integration || 0) < 50"
        @touchstart="makeupBtnTouch"
      >
        <text v-if="!makeupLoading">
          <text class="btn-icon">💎</text>
          确认补签（消耗50积分）
        </text>
        <text v-if="makeupLoading">
          <text class="btn-loading">⟳</text>
          补签中...
        </text>
      </button>
    </view>

    <!-- 7. 签到规则：手风琴折叠+动效 -->
    <view class="rule-section">
      <view class="rule-header" @click="toggleRule">
        <view class="rule-title">
          <text class="title-icon">📜</text>
          <text>签到规则</text>
        </view>
        <view class="rule-toggle" :class="{ 'rule-toggle--open': showRule }">
          {{ showRule ? '▲' : '▼' }}
        </view>
      </view>
      <view class="rule-content" v-if="showRule" :style="{maxHeight: showRule ? '500upx' : '0'}">
        <view class="rule-item" v-for="(rule, idx) in ruleList" :key="idx" :style="{animationDelay: `${idx * 0.1}s`}">
          <view class="rule-num">{{ idx + 1 }}</view>
          <view class="rule-text">{{ rule }}</view>
        </view>
      </view>
    </view>

    <!-- 8. 弹窗反馈（基础功能保留，优化样式） -->
    <view class="popup-mask" v-if="showPopup" @click="closePopup">
      <view class="popup-content" @click.stop>
        <view class="popup-icon" :class="popupType === 'success' ? 'icon--success' : 'icon--error'">
          {{ popupType === 'success' ? '✅' : '⚠️' }}
        </view>
        <view class="popup-title">{{ popupTitle }}</view>
        <view class="popup-desc">{{ popupDesc }}</view>
        <button class="popup-btn" @click.stop="closePopup">
          知道了
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { mapState } from 'vuex';
import { 
  getSignInRecords, doSignIn, doMakeUpSignIn, 
  getContinuousSignInDays, getMemberPoints, getTotalSignDays 
} from '@/api/signin.js';

// 工具函数：日期格式化
const formatApiDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};
const getTodayDate = () => formatApiDate(new Date().toISOString());

// 工具函数：计算今日奖励
const calcTodayReward = (continuousDays) => {
  const rewards = [5, 8, 10, 12, 15, 18, 20];
  return rewards[Math.min(continuousDays, 6)]; // 连续7天及以上保持20积分
};

export default {
  data() {
    return {
      currentYear: new Date().getFullYear(),
      currentMonth: new Date().getMonth() + 1,
      dateList: [],
      signRecords: [],
      totalSignDays: 0,
      continuousDays: 0,
      selectedMakeupDate: '',
      hasMakeupDate: false,
      makeupLoading: false,
      signLoading: false,
      isTodaySigned: false,
      todayReward: 5,
      showRule: false,
      showPopup: false,
      showCelebration: false, // 签到成功庆祝动画
      lastSignIntegration: 0, // 上次签到积分
      lastSignGrowth: 0, // 上次签到成长值
      ruleList: [
        '每日首次签到可获5-20积分，连续天数越多奖励越高',
        '近3天未签到日期可补签，每次消耗50积分',
        '累计签到满30天，额外赠送100积分奖励',
        '连续签到7天/14天/30天，可获专属徽章标识'
      ]
    };
  },
  computed: {
    ...mapState(['hasLogin', 'userInfo']),
    // 判断是否当月页面
    isCurrentMonthPage() {
      const now = new Date();
      return this.currentYear === now.getFullYear() && this.currentMonth === (now.getMonth() + 1);
    },
    // 判断是否禁用上月切换（防止切换到未来月份）
    isPrevMonthDisabled() {
      const now = new Date();
      const prevMonth = new Date(this.currentYear, this.currentMonth - 2);
      const minYear = now.getFullYear() - 1; // 只允许查看近1年
      return prevMonth.getFullYear() < minYear;
    }
  },
  directives: {
    // 自定义指令：数字滚动动画
    'number-animation'(el, binding) {
      const target = Number(binding.value) || 0;
      let current = 0;
      const step = Math.ceil(target / 30); // 30帧完成动画
      const timer = setInterval(() => {
        current += step;
        if (current >= target) {
          current = target;
          clearInterval(timer);
        }
        el.textContent = current;
      }, 30);
      // 解绑时清除定时器
      el.__timer__ = timer;
    }
  },
  beforeUnmount() {
    // 清除所有定时器
    document.querySelectorAll('[number-animation]').forEach(el => {
      clearInterval(el.__timer__);
    });
  },
  onLoad() {
    if (!this.hasLogin) {
      uni.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => this.navBack(), 1500);
      return;
    }
    this.initData();
  },
  methods: {
    async initData() {
      try {
        const [pointsRes, totalDaysRes, recordsRes, continuousRes] = await Promise.all([
          getMemberPoints(),
          getTotalSignDays(),
          getSignInRecords(this.currentYear, this.currentMonth),
          getContinuousSignInDays()
        ]);
        // 更新积分/成长值
        const pointsData = pointsRes.data || {};
        this.$store.commit('updateIntegration', pointsData.integration || 0);
        this.$store.commit('updateGrowth', pointsData.totalGrowth || 0);
        // 更新天数
        this.totalSignDays = totalDaysRes.data || 0;
        this.continuousDays = continuousRes.data || 0;
        this.todayReward = calcTodayReward(this.continuousDays);
        // 处理签到记录
        this.signRecords = (recordsRes.data || []).map(r => ({
          ...r, signInDate: formatApiDate(r.signInDate)
        }));
        this.generateCalendar();
      } catch (err) {
        this.showPopupFn('error', '加载失败', '数据加载出错，请稍后重试');
      }
    },

    generateCalendar() {
      const dateList = [];
      const firstDay = new Date(this.currentYear, this.currentMonth - 1, 1);
      const lastDay = new Date(this.currentYear, this.currentMonth, 0);
      const today = getTodayDate();
      const recent3Days = Array.from({length:3}, (_, i) => {
        const d = new Date();
        d.setDate(d.getDate() - i - 1);
        return formatApiDate(d.toISOString());
      });
      const signedDates = this.signRecords.map(r => r.signInDate);

      this.hasMakeupDate = recent3Days.some(d => !signedDates.includes(d));
      this.isTodaySigned = signedDates.includes(today);

      // 填充上月空白
      const firstDayWeek = firstDay.getDay();
      for (let i = firstDayWeek - 1; i >= 0; i--) {
        const d = new Date(this.currentYear, this.currentMonth - 1, -i);
        dateList.push({
          date: formatApiDate(d.toISOString()),
          day: d.getDate(),
          isCurrentMonth: false,
          isSigned: false,
          canMakeup: false,
          isToday: false
        });
      }

      // 填充当月日期
      for (let i = 1; i <= lastDay.getDate(); i++) {
        const d = new Date(this.currentYear, this.currentMonth - 1, i);
        const dateStr = formatApiDate(d.toISOString());
        const isSigned = signedDates.includes(dateStr);
        const isToday = dateStr === today;
        const canMakeup = recent3Days.includes(dateStr) && !isSigned && !isToday;

        dateList.push({ date: dateStr, day: i, isCurrentMonth: true, isSigned, canMakeup, isToday });
      }

      this.dateList = dateList;
    },

    async changeMonth(step) {
      const newMonth = this.currentMonth + step;
      if (newMonth < 1) {
        this.currentYear--;
        this.currentMonth = 12;
      } else if (newMonth > 12) {
        this.currentYear++;
        this.currentMonth = 1;
      } else {
        this.currentMonth = newMonth;
      }
      try {
        const res = await getSignInRecords(this.currentYear, this.currentMonth);
        this.signRecords = (res.data || []).map(r => ({...r, signInDate: formatApiDate(r.signInDate)}));
        this.generateCalendar();
      } catch (err) {
        this.showPopupFn('error', '切换失败', '获取该月记录失败');
      }
    },

    handleDateClick(date) {
      if (date.canMakeup) {
        this.selectedMakeupDate = date.date;
      } else if (date.isToday && !date.isSigned) {
        this.handleSignToday();
      }
    },

    async handleSignToday() {
      this.signLoading = true;
      try {
        const res = await doSignIn();
        const data = res.data || {};
        if (res.code !== 200) throw new Error(data.message || '签到失败');

        // 计算新数据
        const newInt = (this.userInfo.integration || 0) + (data.integration || 0);
        const newGrowth = (this.userInfo.growth || 0) + (data.growth || 0);
        this.lastSignIntegration = data.integration || 0;
        this.lastSignGrowth = data.growth || 0;

        // 更新状态
        this.$store.commit('updateIntegration', newInt);
        this.$store.commit('updateGrowth', newGrowth);
        this.totalSignDays++;
        this.continuousDays++;
        this.signRecords.push({
          signInDate: getTodayDate(),
          integration: data.integration || 0,
          growth: data.growth || 0
        });
        this.generateCalendar();
        this.signLoading = false;
        // 显示庆祝动画
        this.showCelebration = true;
      } catch (err) {
        this.signLoading = false;
        this.showPopupFn('error', '签到失败', err.message || '请稍后重试');
      }
    },

    async handleMakeup() {
      if (!this.selectedMakeupDate) return this.showPopupFn('error', '补签失败', '请选择补签日期');
      const currentInt = this.userInfo.integration || 0;
      if (currentInt < 50) return this.showPopupFn('error', '补签失败', '积分不足50');

      this.makeupLoading = true;
      try {
        const res = await doMakeUpSignIn(this.selectedMakeupDate);
        const data = res.data || {};
        if (res.code !== 200) throw new Error(data.message || '补签失败');

        // 计算新积分（消耗50+获得补签积分）
        const newInt = currentInt - 50 + (data.integration || 0);
        this.$store.commit('updateIntegration', newInt);
        this.totalSignDays++;
        this.signRecords.push({
          signInDate: this.selectedMakeupDate,
          integration: data.integration || 0,
          growth: data.growth || 0
        });
        this.generateCalendar();
        this.selectedMakeupDate = '';
        this.makeupLoading = false;
        this.showPopupFn('success', '补签成功', `消耗50积分，获得${data.integration || 0}积分，剩余${newInt}分`);
      } catch (err) {
        this.makeupLoading = false;
        this.showPopupFn('error', '补签失败', err.message || '请稍后重试');
      }
    },

    // 辅助方法：根据连续天数获取标题颜色
    getSignStreakColor() {
      if (this.continuousDays >= 30) return '#FF6B6B';
      if (this.continuousDays >= 14) return '#FF9F43';
      if (this.continuousDays >= 7) return '#FFC048';
      return '#95A5A6';
    },

    // 交互动效：按钮触摸反馈
    backBtnTouch(e) {
      e.currentTarget.classList.add('back-btn--active');
      setTimeout(() => e.currentTarget.classList.remove('back-btn--active'), 200);
    },
    signBtnTouch(e) {
      e.currentTarget.classList.add('sign-now-btn--active');
      setTimeout(() => e.currentTarget.classList.remove('sign-now-btn--active'), 200);
    },
    makeupBtnTouch(e) {
      e.currentTarget.classList.add('makeup-btn--active');
      setTimeout(() => e.currentTarget.classList.remove('makeup-btn--active'), 200);
    },
    dateCardTouch(idx) {
      const card = document.querySelectorAll('.date-card')[idx];
      if (card) {
        card.classList.add('date-card--active');
        setTimeout(() => card.classList.remove('date-card--active'), 200);
      }
    },

    // 弹窗/动画控制
    toggleRule() { this.showRule = !this.showRule; },
    showPopupFn(type, title, desc) {
      this.popupType = type;
      this.popupTitle = title;
      this.popupDesc = desc;
      this.showPopup = true;
    },
    closePopup() { this.showPopup = false; },
    closeCelebration() { this.showCelebration = false; },
    navBack() { uni.navigateBack({ delta: 1 }); }
  }
};
</script>

<style scoped>
/* 1. 全局样式：渐变背景+统一字体 */
.sign-page-container {
  min-height: 100vh;
  background: linear-gradient(120deg, #FFF8E1, #FFF3E0);
  padding: 30upx 20upx 80upx;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 2. 顶部标题区 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40upx;
}
.back-btn {
  display: flex;
  align-items: center;
  gap: 8upx;
  color: #FF9800;
  font-size: 24upx;
  padding: 8upx 16upx;
  border-radius: 20upx;
  transition: all 0.2s;
}
.back-btn--active {
  background: rgba(255, 152, 0, 0.1);
  transform: scale(0.95);
}
.back-icon {
  font-size: 28upx;
}
.header-title-container {
  text-align: center;
}
.header-title {
  font-size: 36upx;
  font-weight: 700;
  color: #333;
  margin-bottom: 8upx;
}
.header-subtitle {
  font-size: 20upx;
  opacity: 0.8;
}

/* 3. 核心数据区：立体胶囊卡片 */
.data-section {
  margin-bottom: 40upx;
}
.data-card {
  background: #FFF;
  border-radius: 60upx;
  padding: 30upx 40upx;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 10upx 30upx rgba(255, 152, 0, 0.1);
  transition: all 0.3s;
}
.data-card--glow {
  box-shadow: 0 10upx 40upx rgba(255, 192, 72, 0.2);
}
.data-item {
  text-align: center;
}
.data-label {
  font-size: 22upx;
  color: #666;
  margin-bottom: 12upx;
  position: relative;
  display: inline-block;
}
.streak-badge {
  position: absolute;
  right: -24upx;
  top: -8upx;
  font-size: 24upx;
}
.data-value {
  font-size: 42upx;
  font-weight: 700;
  color: #FF9800;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8upx;
}
.data-unit {
  font-size: 24upx;
  font-weight: 400;
  color: #999;
}
.data-divider {
  width: 2upx;
  height: 80upx;
  background: linear-gradient(transparent, #EEE, transparent);
}

/* 4. 立即签到按钮：特色动效 */
.sign-now-section {
  margin-bottom: 40upx;
  text-align: center;
}
.sign-now-btn {
  width: 90%;
  height: 100upx;
  background: linear-gradient(90deg, #FF9800, #FF7A45);
  color: #FFF;
  border-radius: 50upx;
  font-size: 28upx;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16upx;
  margin: 0 auto 16upx;
  box-shadow: 0 8upx 20upx rgba(255, 122, 69, 0.3);
  transition: all 0.3s;
}
.sign-now-btn--active {
  transform: scale(0.98);
  box-shadow: 0 4upx 12upx rgba(255, 122, 69, 0.2);
}
.sign-icon {
  font-size: 36upx;
  animation: rotate 2s linear infinite;
}
@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.sign-loading {
  font-size: 32upx;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.sign-tip {
  font-size: 20upx;
  color: #666;
}
.tip-highlight {
  color: #FF7A45;
  font-weight: 500;
}

/* 5. 签到成功庆祝动画 */
.success-celebration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 30upx;
}
.celebration-icon {
  font-size: 100upx;
  animation: bounce 1s ease infinite alternate;
}
@keyframes bounce {
  0% { transform: scale(1); }
  100% { transform: scale(1.2); }
}
.celebration-title {
  font-size: 40upx;
  font-weight: 700;
  color: #FF9800;
}
.celebration-desc {
  font-size: 24upx;
  color: #666;
}
.score-fall {
  position: absolute;
  top: -50upx;
  font-size: 36upx;
  animation: fall 2s ease forwards;
  opacity: 0;
}
@keyframes fall {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(800upx) rotate(360deg);
    opacity: 0;
  }
}
.celebration-btn {
  margin-top: 50upx;
  padding: 16upx 60upx;
  background: #FF9800;
  color: #FFF;
  border-radius: 40upx;
  font-size: 26upx;
  border: none;
  box-shadow: 0 6upx 16upx rgba(255, 152, 0, 0.2);
  transition: all 0.2s;
}
.celebration-btn:active {
  transform: scale(0.95);
  box-shadow: 0 4upx 12upx rgba(255, 152, 0, 0.1);
}

/* 6. 日历区域：特色卡片 */
.calendar-section {
  background: #FFF;
  border-radius: 30upx;
  padding: 30upx 20upx;
  margin-bottom: 30upx;
  box-shadow: 0 6upx 20upx rgba(0, 0, 0, 0.05);
}
.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25upx;
}
.month-btn {
  background: transparent;
  border: none;
  color: #FF9800;
  font-size: 28upx;
  width: 50upx;
  height: 50upx;
  border-radius: 50%;
  transition: all 0.2s;
}
.month-btn:active {
  background: rgba(255, 152, 0, 0.1);
}
.month-btn:disabled {
  color: #DDD;
  cursor: not-allowed;
}
.month-title {
  font-size: 26upx;
  font-weight: 600;
  color: #333;
}
.week-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15upx;
}
.week-day {
  width: 14.28%;
  text-align: center;
  font-size: 20upx;
  color: #999;
  font-weight: 500;
}
.date-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12upx;
}
.date-card {
  width: calc(14.28% - 10upx);
  aspect-ratio: 1/1;
  border-radius: 20upx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #F9F9F9;
  font-size: 24upx;
  color: #333;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}
.date-card--empty {
  background: #FAFAFA;
  color: #DDD;
  cursor: default;
}
.date-card--signed {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}
.date-card--today {
  background: rgba(255, 122, 69, 0.1);
  color: #FF7A45;
}
.date-card--makeup {
  background: rgba(255, 152, 0, 0.1);
  color: #FF9800;
  animation: blink 2s ease infinite alternate;
}
@keyframes blink {
  0% { box-shadow: 0 0 0 0 rgba(255, 152, 0, 0.1); }
  100% { box-shadow: 0 0 0 8upx rgba(255, 152, 0, 0.05); }
}
.date-card--selected {
  border: 2upx solid #FF9800;
  box-shadow: 0 4upx 12upx rgba(255, 152, 0, 0.15);
}
.date-card--active {
  transform: translateY(-5upx);
  box-shadow: 0 6upx 16upx rgba(0, 0, 0, 0.08);
}
.date-num {
  font-size: 26upx;
  font-weight: 500;
}
.date-tags {
  position: absolute;
  bottom: 8upx;
  display: flex;
  gap: 6upx;
}
.tag {
  display: flex;
  align-items: center;
  gap: 4upx;
  padding: 2upx 8upx;
  border-radius: 12upx;
  font-size: 14upx;
  font-weight: 500;
}
.tag--today {
  background: #FF7A45;
  color: #FFF;
}
.tag--signed {
  background: #4CAF50;
  color: #FFF;
}
.tag--makeup {
  background: #FF9800;
  color: #FFF;
}
.tag-icon {
  font-size: 16upx;
}
.date-badge {
  position: absolute;
  top: 8upx;
  background: linear-gradient(90deg, #FF9800, #FF7A45);
  color: #FFF;
  font-size: 12upx;
  padding: 2upx 8upx;
  border-radius: 10upx;
  font-weight: 500;
}

/* 7. 补签区域 */
.makeup-section {
  background: #FFF;
  border-radius: 30upx;
  padding: 30upx 20upx;
  margin-bottom: 30upx;
  box-shadow: 0 6upx 20upx rgba(0, 0, 0, 0.05);
}
.makeup-header {
  margin-bottom: 25upx;
}
.makeup-title {
  display: flex;
  align-items: center;
  gap: 12upx;
  font-size: 26upx;
  font-weight: 600;
  color: #333;
  margin-bottom: 12upx;
}
.title-icon {
  color: #FF9800;
  font-size: 28upx;
}
.makeup-desc {
  font-size: 20upx;
  color: #666;
  line-height: 30upx;
}
.desc-highlight {
  color: #FF7A45;
  font-weight: 500;
}
.makeup-selected {
  display: flex;
  align-items: center;
  gap: 12upx;
  background: rgba(255, 152, 0, 0.05);
  padding: 16upx 20upx;
  border-radius: 20upx;
  margin-bottom: 25upx;
  font-size: 22upx;
  color: #333;
}
.selected-icon {
  color: #FF9800;
  font-size: 24upx;
}
.makeup-cost {
  margin-left: 16upx;
  font-size: 20upx;
  color: #F44336;
  display: flex;
  align-items: center;
  gap: 8upx;
}
.cost-icon {
  font-size: 22upx;
}
.makeup-btn {
  width: 100%;
  height: 90upx;
  background: linear-gradient(90deg, #FF9800, #FF7A45);
  color: #FFF;
  border-radius: 45upx;
  font-size: 26upx;
  font-weight: 500;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12upx;
  box-shadow: 0 6upx 16upx rgba(255, 122, 69, 0.2);
  transition: all 0.2s;
}
.makeup-btn--active {
  transform: scale(0.98);
  box-shadow: 0 4upx 12upx rgba(255, 122, 69, 0.15);
}
.makeup-btn:disabled {
  background: #FFCC80;
  box-shadow: none;
  cursor: not-allowed;
}
.btn-loading {
  animation: spin 1s linear infinite;
  font-size: 28upx;
}
.btn-icon {
  font-size: 28upx;
}

/* 8. 签到规则：手风琴效果 */
.rule-section {
  background: #FFF;
  border-radius: 30upx;
  padding: 30upx 20upx;
  box-shadow: 0 6upx 20upx rgba(0, 0, 0, 0.05);
}
.rule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15upx;
  border-bottom: 1upx solid #EEE;
  cursor: pointer;
}
.rule-title {
  display: flex;
  align-items: center;
  gap: 12upx;
  font-size: 26upx;
  font-weight: 600;
  color: #333;
}
.rule-toggle {
  color: #FF9800;
  font-size: 24upx;
  transition: all 0.3s;
}
.rule-toggle--open {
  transform: rotate(180deg);
}
.rule-content {
  overflow: hidden;
  transition: max-height 0.5s ease;
  margin-top: 20upx;
}
.rule-item {
  display: flex;
  align-items: flex-start;
  gap: 16upx;
  margin-bottom: 20upx;
  opacity: 0;
  animation: fadeIn 0.5s ease forwards;
}
@keyframes fadeIn {
  0% { opacity: 0; transform: translateY(10upx); }
  100% { opacity: 1; transform: translateY(0); }
}
.rule-num {
  width: 32upx;
  height: 32upx;
  border-radius: 50%;
  background: #FF9800;
  color: #FFF;
  font-size: 20upx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 4upx;
}
.rule-text {
  font-size: 20upx;
  color: #666;
  line-height: 32upx;
}

/* 9. 弹窗样式优化 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(8upx);
}
.popup-content {
  background: #FFF;
  border-radius: 30upx;
  padding: 50upx 30upx;
  width: 80%;
  max-width: 500upx;
  text-align: center;
  box-shadow: 0 10upx 40upx rgba(0, 0, 0, 0.15);
}
.popup-icon {
  font-size: 72upx;
  margin-bottom: 25upx;
}
.icon--success {
  color: #4CAF50;
}
.icon--error {
  color: #F44336;
}
.popup-title {
  font-size: 32upx;
  font-weight: 600;
  color: #333;
  margin-bottom: 15upx;
}
.popup-desc {
  font-size: 22upx;
  color: #666;
  line-height: 34upx;
  margin-bottom: 40upx;
}
.popup-btn {
  width: 60%;
  height: 80upx;
  background: #FF9800;
  color: #FFF;
  border-radius: 40upx;
  font-size: 26upx;
  border: none;
  transition: all 0.2s;
}
.popup-btn:active {
  transform: scale(0.95);
  background: #F57C00;
}
</style>