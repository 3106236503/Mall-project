<template>
  <div class="app-container">
    <!-- 筛选搜索区域 -->
    <el-card class="filter-container" shadow="hover" :body-style="{ padding: '20px' }">
      <div class="filter-header">
        <i class="el-icon-search filter-icon"></i>
        <span class="filter-title">筛选搜索</span>
      </div>
      <div style="margin-top: 20px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="会员名称：">
            <el-input
              v-model="listQuery.nickname"
              class="input-width"
              placeholder="请输入会员名称"
              clearable
              prefix-icon="el-icon-user"
            ></el-input>
          </el-form-item>
          <el-form-item label="会员等级：">
            <el-select
              v-model="listQuery.memberLevelId"
              placeholder="请选择会员等级"
              clearable
              style="width: 200px"
            >
              <el-option v-for="level in memberLevels" :key="level.id" :label="level.name" :value="level.id"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="filter-actions">
        <el-button
          type="primary"
          @click="handleSearchList()"
          size="small"
          icon="el-icon-search"
          class="search-btn"
        >
          查询搜索
        </el-button>
        <el-button
          @click="handleResetSearch()"
          size="small"
          icon="el-icon-refresh"
          class="reset-btn"
        >
          重置
        </el-button>
      </div>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="operate-container" shadow="hover" :body-style="{ padding: '15px 20px' }">
      <div class="operate-header">
        <i class="el-icon-tickets operate-icon"></i>
        <span class="operate-title">会员列表</span>
        <el-button
          size="mini"
          type="primary"
          @click="handleAdd()"
          icon="el-icon-plus"
          class="btn-add"
        >
          添加会员
        </el-button>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table
        ref="memberTable"
        :data="list"
        style="width: 100%;"
        v-loading="listLoading"
        border
        stripe
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
        :cell-style="{ padding: '12px 8px' }"
        class="member-table"
      >
        <el-table-column label="会员编号" width="100" align="center">
          <template slot-scope="scope">
            <span class="member-id">{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="会员名称" align="center" min-width="120">
          <template slot-scope="scope">
            <div class="member-name">
              <i class="el-icon-user"></i>
              {{ scope.row.nickname }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="会员等级" align="center" width="120">
          <template slot-scope="scope">
            <el-tag
              v-if="getLevelName(scope.row.memberLevelId)"
              :type="getLevelTagType(scope.row.memberLevelId)"
              class="level-tag"
            >
              {{ getLevelName(scope.row.memberLevelId) }}
            </el-tag>
            <el-tag v-else type="info">普通会员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="手机号码" align="center" width="130">
          <template slot-scope="scope">
            <div class="phone-number">
              <i class="el-icon-phone"></i>
              {{ scope.row.phone || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="积分" width="100" align="center">
          <template slot-scope="scope">
            <span class="integration-value">{{ scope.row.integration || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" align="center">
          <template slot-scope="scope">
            <div class="time-info">
              <i class="el-icon-time"></i>
              {{ scope.row.createTime | formatDateTime }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" width="160" align="center">
          <template slot-scope="scope">
            <div class="time-info">
              <i class="el-icon-success"></i>
              {{ scope.row.lastLoginTime | formatDateTime }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账户余额" align="center" width="120">
          <template slot-scope="scope">
            <span class="balance-amount">{{ scope.row.balance || 0 }} 元</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template slot-scope="scope">
            <el-switch
              @change="handleStatusChange(scope.$index, scope.row)"
              :active-value="1"
              :inactive-value="0"
              v-model="scope.row.status"
              active-color="#13ce66"
              inactive-color="#ff4949"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button-group>
              <el-button
                size="mini"
                type="primary"
                @click="handleView(scope.$index, scope.row)"
                icon="el-icon-view"
                class="action-btn"
              ></el-button>
              <el-button
                size="mini"
                type="success"
                @click="handleUpdate(scope.$index, scope.row)"
                icon="el-icon-edit"
                class="action-btn"
              ></el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)"
                icon="el-icon-delete"
                class="action-btn"
              ></el-button>
            </el-button-group>
            <el-button
              size="mini"
              type="info"
              @click="handleRecharge(scope.$index, scope.row)"
              icon="el-icon-plus"
              class="action-btn"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        @prev-click="handlePrevClick"
        @next-click="handleNextClick"
        layout="total, sizes, prev, pager, next, jumper"
        v-model="currentPage"
        :page-size="listQuery.pageSize"
        :page-sizes="[10, 15, 20]"
        :pager-count="7"
        :total="total"
        class="custom-pagination"
      ></el-pagination>
    </div>

    <!-- 会员详情对话框 -->
    <el-dialog
      title="会员详情"
      :visible.sync="detailDialogVisible"
      width="70%"
      class="detail-dialog"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab" type="card" class="member-detail-tabs">
        <!-- 基本信息标签页 -->
        <el-tab-pane label="基本信息" name="baseInfo">
          <el-descriptions :column="2" border class="member-details">
            <el-descriptions-item label="会员编号" label-class-name="detail-label">
              <span class="detail-value">{{ detailMember.id }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="积分" label-class-name="detail-label">
              <span class="detail-value integration">{{ detailMember.integration || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="会员名称" label-class-name="detail-label">
              <span class="detail-value">{{ detailMember.nickname }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="手机号码" label-class-name="detail-label">
              <span class="detail-value">{{ detailMember.phone }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱" label-class-name="detail-label">
              <span class="detail-value">{{ detailMember.email || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="会员等级" label-class-name="detail-label">
              <el-tag type="primary">{{ getLevelName(detailMember.memberLevelId) || '普通会员' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="账户余额" label-class-name="detail-label">
              <span class="detail-value balance">{{ detailMember.balance || 0 }} 元</span>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间" label-class-name="detail-label">
              <span class="detail-value">{{ detailMember.createTime | formatDateTime }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="最后登录" label-class-name="detail-label">
              <span class="detail-value">{{ detailMember.lastLoginTime | formatDateTime }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态" span="2" label-class-name="detail-label">
              <el-tag :type="detailMember.status === 1 ? 'success' : 'danger'" class="status-tag">
                {{ detailMember.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 积分记录标签页 -->
        <el-tab-pane label="积分记录" name="pointRecord">
          <div class="point-record-container">
            <!-- 筛选区域 -->
            <div class="point-record-filter" style="margin-bottom: 15px; display: flex; justify-content: flex-end;">
              <el-form :inline="true" :model="pointRecordQuery" size="mini">
                <el-form-item label="变动方向：">
                  <el-select v-model="pointRecordQuery.changeType" placeholder="全部方向" clearable style="width: 120px;">
                    <el-option label="全部方向" value=""></el-option>
                    <el-option label="积分增加" value="0"></el-option>
                    <el-option label="积分减少" value="1"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="积分来源：">
                  <el-select v-model="pointRecordQuery.sourceType" placeholder="全部来源" clearable style="width: 120px;">
                    <el-option label="全部来源" value=""></el-option>
                    <el-option label="购物" value="0"></el-option>
                    <el-option label="管理员修改" value="1"></el-option>
                    <el-option label="充值" value="2"></el-option>
                    <el-option label="签到" value="3"></el-option>
                    <el-option label="补签" value="4"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="时间范围：">
                  <el-date-picker
                    v-model="pointRecordQuery.timeRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    size="mini"
                    style="width: 240px;"
                  ></el-date-picker>
                </el-form-item>
                <el-button type="primary" size="mini" @click="getPointRecord(detailMember.id)" icon="el-icon-search">查询</el-button>
                <el-button size="mini" @click="resetPointRecordQuery()" icon="el-icon-refresh">重置</el-button>
              </el-form>
            </div>

            <!-- 积分记录表格 -->
            <el-table
              v-loading="pointRecordLoading"
              :data="pointRecordList"
              border
              stripe
              :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
              :cell-style="{ padding: '12px 8px' }"
              class="point-record-table"
            >
              <el-table-column label="变动时间" width="180" align="center">
                <template slot-scope="scope">{{ scope.row.createTime | formatDateTime }}</template>
              </el-table-column>
              <el-table-column label="变动类型" width="160" align="center">
                <template slot-scope="scope">
                  <el-tag :type="getPointTypeTag(scope.row.changeType)">
                    {{ getPointTypeName(scope.row) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="变动积分" width="120" align="center">
                <template slot-scope="scope">
                  <span :class="scope.row.changeType === 0 ? 'text-success' : 'text-danger'">
                    {{ scope.row.changeType === 0 ? '+' : '-' }}{{ scope.row.changeCount }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作人" width="120" align="center">
                <template slot-scope="scope">
                  {{ scope.row.operateMan || '系统' }}
                </template>
              </el-table-column>
              <el-table-column label="备注" align="center" min-width="150">
                <template slot-scope="scope">
                  {{ scope.row.operateNote || '-' }}
                </template>
              </el-table-column>
            </el-table>

            <!-- 积分记录分页 -->
            <div class="pagination-container" style="margin-top: 15px;">
              <el-pagination
                background
                @size-change="handlePointRecordSizeChange"
                @current-change="handlePointRecordCurrentChange"
                layout="total, sizes, prev, pager, next, jumper"
                v-model="pointRecordCurrentPage"
                :page-size="pointRecordPageSize"
                :page-sizes="[10, 15, 20]"
                :total="pointRecordTotal"
                class="custom-pagination"
              ></el-pagination>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false" size="small" class="close-btn">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 编辑/添加会员对话框 -->
    <el-dialog
      :title="isEdit?'编辑会员':'添加会员'"
      :visible.sync="dialogVisible"
      width="50%"
      class="edit-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="member" ref="memberForm" label-width="120px" size="small" class="member-form">
        <el-form-item label="会员名称：">
          <el-input
            v-model="member.nickname"
            style="width: 300px"
            placeholder="请输入会员名称"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户名：" prop="username">
          <el-input
            v-model="member.username"
            style="width: 300px"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码：" prop="password">
          <el-input
            v-model="member.password"
            type="password"
            style="width: 300px"
            placeholder="请输入密码"
            :show-password="true"
            prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>
        <el-form-item label="手机号码：">
          <el-input
            v-model="member.phone"
            style="width: 300px"
            placeholder="请输入手机号码"
            prefix-icon="el-icon-phone"
          ></el-input>
        </el-form-item>
        <el-form-item label="积分：">
          <el-input
            v-model="member.integration"
            type="number"
            style="width: 300px"
            placeholder="请输入积分"
            prefix-icon="el-icon-coin"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮箱：">
          <el-input
            v-model="member.email"
            style="width: 300px"
            placeholder="请输入邮箱"
            prefix-icon="el-icon-message"
          ></el-input>
        </el-form-item>
        <el-form-item label="会员等级：">
          <el-select
            v-model="member.memberLevelId"
            placeholder="请选择会员等级"
            style="width: 300px"
            prefix-icon="el-icon-rank"
          >
            <el-option v-for="level in memberLevels" :key="level.id" :label="level.name" :value="level.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="账户余额：">
          <el-input
            v-model="member.balance"
            type="number"
            style="width: 300px"
            placeholder="请输入账户余额"
            prefix-icon="el-icon-money"
          ></el-input>
        </el-form-item>
        <el-form-item label="是否启用：">
          <el-radio-group v-model="member.status">
            <el-radio :label="1" class="status-radio">启用</el-radio>
            <el-radio :label="0" class="status-radio">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm()" size="small" class="confirm-btn">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 积分充值对话框（修复Vue 2语法：移除可选链，用短路判断） -->
    <el-dialog
      title="积分充值"
      :visible.sync="rechargeDialogVisible"
      width="500px"
      class="recharge-dialog"
      :close-on-click-modal="false"
    >
      <div class="recharge-header">
        <div class="member-info-card">
          <!-- 关键修复：Vue 2不支持?.，改用&&短路判断 -->
          <div class="info-item">
            <span class="info-label">会员编号：</span>
            <span class="info-value">{{ rechargeForm.memberInfo && rechargeForm.memberInfo.id }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">会员名称：</span>
            <span class="info-value">{{ rechargeForm.memberInfo && rechargeForm.memberInfo.nickname }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">用户名：</span>
            <span class="info-value">{{ rechargeForm.memberInfo && rechargeForm.memberInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">当前积分：</span>
            <span class="info-value current-integration">{{ (rechargeForm.memberInfo && rechargeForm.memberInfo.currentIntegration) || 0 }}</span>
          </div>
        </div>
      </div>

      <el-form :model="rechargeForm" label-width="100px" size="small" class="recharge-form">
        <el-form-item label="充值积分：" required>
          <el-input
            v-model.number="rechargeForm.integration"
            placeholder="请输入充值积分数"
            type="number"
            min="1"
            class="recharge-input"
          >
            <template slot="prepend">
              <i class="el-icon-coin"></i>
            </template>
          </el-input>
        </el-form-item>

        <!-- 备注输入框 -->
        <el-form-item label="备注说明：">
          <el-input
            v-model="rechargeForm.operateNote"
            placeholder="请输入充值备注（如：双11活动充值、补赠积分）"
            type="textarea"
            :rows="3"
            class="remark-input"
            clearable
          >
            <template slot="prepend">
              <i class="el-icon-document"></i>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <div class="recharge-note">
        <i class="el-icon-info"></i>
        积分充值后不可撤回，请谨慎操作
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="rechargeDialogVisible = false" size="small" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="handleRechargeConfirm" size="small" class="confirm-btn">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  fetchList,
  createMember,
  updateMember,
  deleteMember,
  getMemberInfo,
  updateIntegration,
  fetchPointRecord
} from '@/api/member';
import { fetchAvailableList } from '@/api/memberLevel';
import { formatDate } from '@/utils/date';

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  nickname: null,
  memberLevelId: null
};
const defaultMember = {
  username: undefined,
  password: undefined,
  id: null,
  nickname: null,
  phone: null,
  email: null,
  memberLevelId: null,
  balance: 0,
  status: 1
};

export default {
  name: 'memberList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      currentPage: 1,
      listLoading: false,
      dialogVisible: false,
      detailDialogVisible: false,
      member: Object.assign({}, defaultMember),
      detailMember: Object.assign({}, defaultMember),
      isEdit: false,
      memberLevels: [],
      rechargeDialogVisible: false,
      rechargeForm: {
        memberId: null,
        integration: 0,
        operateNote: '', // 初始化备注字段
        memberInfo: {
          id: null,
          nickname: '',
          username: '',
          currentIntegration: 0
        }
      },
      activeTab: 'baseInfo',
      pointRecordList: [],
      pointRecordLoading: false,
      pointRecordCurrentPage: 1,
      pointRecordPageSize: 10,
      pointRecordTotal: 0,
      pointRecordQuery: {
        changeType: '',
        sourceType: '',
        timeRange: []
      }
    };
  },
  watch: {
    currentPage(newVal) {
      this.listQuery.pageNum = newVal;
    },
    'listQuery.pageNum'(newVal) {
      this.currentPage = newVal;
    },
    activeTab(newVal) {
      if (newVal === 'pointRecord' && this.detailMember.id) {
        this.getPointRecord(this.detailMember.id);
      }
    }
  },
  created() {
    this.getList();
    this.getMemberLevels();
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') return 'N/A';
      const date = typeof time === 'string' ? new Date(time) : time;
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss');
    }
  },
  methods: {
    getLevelTagType(levelId) {
      const types = ['', 'success', 'warning', 'danger', 'info'];
      return types[levelId % types.length] || 'primary';
    },

    handleResetSearch() {
      this.listQuery = Object.assign({}, defaultListQuery);
    },

    handleSearchList() {
      this.listQuery.pageNum = 1;
      this.getList();
    },

    handleSizeChange(val) {
      this.listQuery.pageNum = 1;
      this.currentPage = 1;
      this.listQuery.pageSize = val;
      this.getList();
    },

    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.currentPage = val;
      this.getList();
    },

    handlePrevClick(val) {
      this.listQuery.pageNum = val;
      this.currentPage = val;
      this.getList();
    },

    handleNextClick(val) {
      this.listQuery.pageNum = val;
      this.currentPage = val;
      this.getList();
    },

    handleAdd() {
      this.dialogVisible = true;
      this.isEdit = false;
      this.member = Object.assign({}, defaultMember);
      if (this.memberLevels.length > 0) {
        this.member.memberLevelId = this.memberLevels[0].id;
      }
    },

    handleStatusChange(index, row) {
      this.$confirm('是否修改该会员状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateMember({ id: row.id, status: row.status }).then(() => {
          this.$message({ type: 'success', message: '修改成功!' });
        }).catch(() => {
          this.$message({ type: 'error', message: '修改失败!' });
          this.getList();
        });
      }).catch(() => {
        this.$message({ type: 'info', message: '取消修改' });
        this.getList();
      });
    },

    handleDelete(index, row) {
      this.$confirm('是否删除该会员?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember(row.id).then(response => {
          if (response && response.code === 200) {
            this.$message({ type: 'success', message: '删除成功!' });
            this.getList();
          } else {
            this.$message({ type: 'error', message: (response && response.message) || '删除失败!' });
          }
        }).catch(() => {
          this.$message({ type: 'error', message: '网络错误，请稍后重试!' });
        });
      }).catch(() => {
        this.$message({ type: 'info', message: '取消删除' });
      });
    },

    handleUpdate(index, row) {
      this.dialogVisible = true;
      this.isEdit = true;
      this.member = Object.assign({}, row);
    },

    handleView(index, row) {
      this.detailDialogVisible = true;
      this.activeTab = 'baseInfo';
      this.pointRecordCurrentPage = 1;
      this.resetPointRecordQuery();

      this.$loading({
        lock: true,
        text: '加载中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      getMemberInfo(row.id).then(response => {
        this.detailMember = (response && response.data) || Object.assign({}, defaultMember);
        if (!(response && response.data)) {
          this.$message.warning('获取会员详情数据不完整');
        }
      }).catch(() => {
        this.$message.error('获取会员详情失败，请稍后重试');
        this.detailDialogVisible = false;
        this.detailMember = Object.assign({}, defaultMember);
      }).finally(() => {
        this.$loading().close();
      });
    },

    handleDialogConfirm() {
      this.$confirm('是否确认操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const request = this.isEdit ? updateMember(this.member) : createMember(this.member);
        request.then(() => {
          this.$message({ type: 'success', message: this.isEdit ? '修改成功!' : '添加成功!' });
          this.dialogVisible = false;
          this.getList();
        });
      });
    },

    handleRecharge(index, row) {
      this.rechargeForm = {
        memberId: row.id,
        integration: 0,
        operateNote: '', // 每次打开重置备注
        memberInfo: {
          id: row.id,
          nickname: row.nickname,
          username: row.username,
          currentIntegration: row.integration || 0
        }
      };
      this.rechargeDialogVisible = true;
    },

    handleRechargeConfirm() {
      if (!this.rechargeForm.integration || this.rechargeForm.integration <= 0) {
        this.$message({ type: 'warning', message: '请输入有效的积分数' });
        return;
      }

      updateIntegration(this.rechargeForm.memberId, {
        integration: this.rechargeForm.integration,
        operateNote: this.rechargeForm.operateNote
      }).then(() => {
        this.$message({ type: 'success', message: '充值成功!' });
        this.rechargeDialogVisible = false;
        this.getList();
        if (this.detailDialogVisible && this.detailMember.id === this.rechargeForm.memberId) {
          this.getPointRecord(this.rechargeForm.memberId);
        }
      }).catch((error) => {
        console.error('充值失败：', error);
        this.$message({ type: 'error', message: '充值失败!' });
      });
    },

    getList() {
      this.listLoading = true;
      fetchList(this.listQuery).then(response => {
        const allMembers = (response && response.data && response.data.list) || [];
        let filteredMembers = [...allMembers];

        if (this.listQuery.nickname) {
          filteredMembers = filteredMembers.filter(m =>
            (m.nickname && m.nickname.toLowerCase()).includes(this.listQuery.nickname.toLowerCase())
          );
        }
        if (this.listQuery.memberLevelId) {
          filteredMembers = filteredMembers.filter(m => m.memberLevelId === this.listQuery.memberLevelId);
        }

        this.total = filteredMembers.length;
        const { pageNum, pageSize } = this.listQuery;
        const start = (pageNum - 1) * pageSize;
        this.list = filteredMembers.slice(start, start + pageSize);
      }).catch(() => {
        this.list = [];
        this.total = 0;
      }).finally(() => {
        this.listLoading = false;
      });
    },

    getMemberLevels() {
      fetchAvailableList({}).then(response => {
        if (response && response.data) {
          this.memberLevels = Array.isArray(response.data)
            ? response.data
            : (response.data.list || []);
        } else {
          this.memberLevels = [];
        }
      }).catch(() => {
        this.memberLevels = [];
        this.$message.error('获取会员等级失败，请检查网络');
      });
    },

    getLevelName(levelId) {
      if (!levelId || !this.memberLevels.length) return null;
      const level = this.memberLevels.find(item => item.id === levelId);
      return level ? level.name : null;
    },

    getPointRecord(memberId) {
      if (!memberId) return;

      this.pointRecordLoading = true;
      const params = {
        memberId: memberId,
        pageNum: this.pointRecordCurrentPage,
        pageSize: this.pointRecordPageSize,
        changeType: this.pointRecordQuery.changeType || undefined,
        sourceType: this.pointRecordQuery.sourceType || undefined,
        startTime: this.pointRecordQuery.timeRange[0]
          ? new Date(this.pointRecordQuery.timeRange[0]).getTime()
          : undefined,
        endTime: this.pointRecordQuery.timeRange[1]
          ? new Date(this.pointRecordQuery.timeRange[1]).getTime() + 24 * 60 * 60 * 1000 - 1
          : undefined
      };

      fetchPointRecord(params).then(response => {
        this.pointRecordLoading = false;
        if (response && response.code === 200 && response.data) {
          this.pointRecordList = response.data.list || [];
          this.pointRecordTotal = response.data.total || 0;
        } else {
          this.pointRecordList = [];
          this.pointRecordTotal = 0;
          this.$message.warning('未查询到积分记录');
        }
      }).catch(error => {
        this.pointRecordLoading = false;
        this.pointRecordList = [];
        this.pointRecordTotal = 0;
        const errorMsg = (error && error.response && error.response.data && error.response.data.message) || '网络异常';
        this.$message.error('获取积分记录失败：' + errorMsg);
      });
    },

    handlePointRecordSizeChange(val) {
      this.pointRecordPageSize = val;
      this.pointRecordCurrentPage = 1;
      this.getPointRecord(this.detailMember.id);
    },

    handlePointRecordCurrentChange(val) {
      this.pointRecordCurrentPage = val;
      this.getPointRecord(this.detailMember.id);
    },

    resetPointRecordQuery() {
      this.pointRecordQuery = {
        changeType: '',
        sourceType: '',
        timeRange: []
      };
    },

    getPointTypeName(row) {
      const sourceMap = {
        0: '购物',
        1: '管理员修改',
        2: '充值',
        3: '签到',
        4: '补签'
      };
      const changeMap = {
        0: '积分增加',
        1: '积分减少'
      };
      const sourceText = sourceMap[row.sourceType] || `未知来源(${row.sourceType})`;
      const changeText = changeMap[row.changeType] || `未知变动(${row.changeType})`;
      return `${sourceText}-${changeText}`;
    },

    getPointTypeTag(changeType) {
      switch (changeType) {
        case 0: return 'success';
        case 1: return 'danger';
        default: return 'primary';
      }
    }
  }
};
</script>

<style scoped>
/* 全局容器 */
.app-container {
  padding: 20px;
  background: #f5f7fa;
}

/* 筛选区域 */
.filter-container {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}
.filter-container:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}
.filter-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
.filter-icon {
  color: #409EFF;
  font-size: 18px;
  margin-right: 8px;
}
.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.filter-actions {
  text-align: right;
  margin-top: 20px;
}
.search-btn {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
  border-radius: 4px;
}
.reset-btn {
  border-radius: 4px;
}

/* 操作区域 */
.operate-container {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}
.operate-container:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}
.operate-header {
  display: flex;
  align-items: center;
}
.operate-icon {
  color: #67C23A;
  font-size: 18px;
  margin-right: 8px;
}
.operate-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.btn-add {
  background: linear-gradient(45deg, #67C23A, #85ce61);
  border: none;
  border-radius: 4px;
  margin-left: 20px;
}

/* 会员表格 */
.table-container {
  margin-bottom: 20px;
}
.member-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.member-table >>> .el-table__body tr:hover > td {
  background-color: #f0f9ff !important;
}
.member-id {
  font-weight: 600;
  color: #409EFF;
}
.member-name {
  display: flex;
  align-items: center;
  justify-content: center;
}
.member-name i {
  margin-right: 5px;
  color: #909399;
}
.level-tag {
  font-weight: 500;
}
.phone-number {
  display: flex;
  align-items: center;
  justify-content: center;
}
.phone-number i {
  margin-right: 5px;
  color: #67C23A;
}
.integration-value {
  font-weight: 600;
  color: #E6A23C;
}
.time-info {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.time-info i {
  margin-right: 5px;
  color: #909399;
}
.balance-amount {
  font-weight: 600;
  color: #F56C6C;
}
.action-btn {
  border-radius: 4px;
  padding: 7px 9px;
  margin-right: 5px;
}

/* 分页 */
.pagination-container {
  text-align: center;
  margin-top: 20px;
}
.custom-pagination {
  justify-content: center;
}

/* 详情对话框 */
.detail-dialog >>> .el-dialog {
  border-radius: 12px;
}
.detail-dialog >>> .el-dialog__header {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border-radius: 12px 12px 0 0;
  padding: 15px 20px;
}
.detail-dialog >>> .el-dialog__title {
  color: white;
  font-weight: 600;
}
.member-details {
  margin: 20px 0;
}
.detail-label {
  background: #f5f7fa;
  font-weight: 600;
}
.detail-value {
  font-weight: 500;
}
.detail-value.integration {
  color: #E6A23C;
}
.detail-value.balance {
  color: #F56C6C;
}
.status-tag {
  font-weight: 600;
}
.close-btn {
  border-radius: 4px;
}

/* 编辑对话框 */
.edit-dialog >>> .el-dialog {
  border-radius: 12px;
}
.edit-dialog >>> .el-dialog__header {
  background: linear-gradient(45deg, #67C23A, #85ce61);
  border-radius: 12px 12px 0 0;
  padding: 15px 20px;
}
.edit-dialog >>> .el-dialog__title {
  color: white;
  font-weight: 600;
}
.member-form >>> .el-form-item {
  margin-bottom: 18px;
}
.status-radio {
  margin-right: 20px;
}
.cancel-btn {
  border-radius: 4px;
}
.confirm-btn {
  background: linear-gradient(45deg, #67C23A, #85ce61);
  border: none;
  border-radius: 4px;
}

/* 充值对话框 */
.recharge-dialog >>> .el-dialog {
  border-radius: 12px;
}
.recharge-dialog >>> .el-dialog__header {
  background: linear-gradient(45deg, #e6a23c, #ebb563);
  border-radius: 12px 12px 0 0;
  padding: 15px 20px;
}
.recharge-dialog >>> .el-dialog__title {
  color: white;
  font-weight: 600;
}
.recharge-header {
  margin-bottom: 20px;
}
.member-info-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  border: 1px solid #ebeef5;
}
.info-item {
  display: flex;
  margin-bottom: 8px;
}
.info-item:last-child {
  margin-bottom: 0;
}
.remark-input {
  width: 250px !important;
  height: 100px;
  resize: none;
}
.info-label {
  font-weight: 600;
  color: #606266;
  width: 80px;
  text-align: right;
  margin-right: 10px;
}
.info-value {
  color: #303133;
  flex: 1;
}
.current-integration {
  color: #e6a23c;
  font-weight: 600;
}
.recharge-form {
  margin-bottom: 15px;
}
.recharge-input {
  width: 250px;
}
.recharge-note {
  background: #fdf6ec;
  border: 1px solid #fbe0c2;
  border-radius: 4px;
  padding: 10px;
  font-size: 12px;
  color: #e6a23c;
}
.recharge-note i {
  margin-right: 5px;
}

/* 积分记录标签页 */
.member-detail-tabs >>> .el-tabs__header {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}
.member-detail-tabs >>> .el-tabs__item {
  font-size: 14px;
  font-weight: 500;
  padding: 10px 20px;
}
.member-detail-tabs >>> .el-tabs__active-bar {
  background-color: #409EFF;
  height: 3px;
}
.member-detail-tabs >>> .el-tabs__item.is-active {
  color: #409EFF;
}
.point-record-container {
  padding: 0 5px;
}
.point-record-table >>> .el-table {
  border-radius: 8px;
  overflow: hidden;
}
.point-record-table >>> .el-table__body tr:hover > td {
  background-color: #f0f9ff !important;
}
.text-success {
  color: #67C23A;
  font-weight: 500;
}
.text-danger {
  color: #F56C6C;
  font-weight: 500;
}
.point-record-filter >>> .el-form-item {
  margin-bottom: 0;
  margin-left: 10px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .app-container {
    padding: 10px;
  }
  .filter-actions {
    text-align: center;
  }
  .operate-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .btn-add {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
  }
  .detail-dialog >>> .el-dialog {
    width: 95% !important;
  }
  .point-record-table >>> .el-table__header th {
    font-size: 12px;
  }
  .point-record-table >>> .el-table__body td {
    font-size: 11px;
  }
  .point-record-filter {
    flex-direction: column;
    align-items: flex-start !important;
  }
  .point-record-filter >>> .el-form {
    width: 100%;
  }
  .point-record-filter >>> .el-form-item {
    margin-bottom: 10px;
    margin-left: 0;
    width: 100%;
  }
  .point-record-filter >>> .el-select,
  .point-record-filter >>> .el-date-picker {
    width: 100% !important;
  }
}
</style>
