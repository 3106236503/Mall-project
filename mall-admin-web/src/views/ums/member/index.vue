<template>
  <div class="app-container">
    <!-- 筛选搜索区域 - 美化 -->
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

    <!-- 操作区域 - 美化 -->
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

    <!-- 表格区域 - 美化 -->
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
            <span class="member-id">{{scope.row.id}}</span>
          </template>
        </el-table-column>
        <el-table-column label="会员名称" align="center" min-width="120">
          <template slot-scope="scope">
            <div class="member-name">
              <i class="el-icon-user"></i>
              {{scope.row.nickname}}
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
              {{getLevelName(scope.row.memberLevelId)}}
            </el-tag>
            <el-tag v-else type="info">普通会员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="手机号码" align="center" width="130">
          <template slot-scope="scope">
            <div class="phone-number">
              <i class="el-icon-phone"></i>
              {{scope.row.phone || '-'}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="积分" width="100" align="center">
          <template slot-scope="scope">
            <span class="integration-value">{{scope.row.integration || 0}}</span>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" align="center">
          <template slot-scope="scope">
            <div class="time-info">
              <i class="el-icon-time"></i>
              {{scope.row.createTime | formatDateTime}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" width="160" align="center">
          <template slot-scope="scope">
            <div class="time-info">
              <i class="el-icon-success"></i>
              {{scope.row.lastLoginTime | formatDateTime}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账户余额" align="center" width="120">
          <template slot-scope="scope">
            <span class="balance-amount">{{scope.row.balance || 0}} 元</span>
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
            >
            </el-switch>
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
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 - 美化 -->
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
      >
      </el-pagination>
    </div>

    <!-- 会员详情对话框 - 美化 -->
    <el-dialog
      title="会员详情"
      :visible.sync="detailDialogVisible"
      width="60%"
      class="detail-dialog"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border class="member-details">
        <el-descriptions-item label="会员编号" label-class-name="detail-label">
          <span class="detail-value">{{detailMember.id}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="积分" label-class-name="detail-label">
          <span class="detail-value integration">{{detailMember.integration || 0}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="会员名称" label-class-name="detail-label">
          <span class="detail-value">{{detailMember.nickname}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="手机号码" label-class-name="detail-label">
          <span class="detail-value">{{detailMember.phone}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="邮箱" label-class-name="detail-label">
          <span class="detail-value">{{detailMember.email || '-'}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="会员等级" label-class-name="detail-label">
          <el-tag type="primary">{{getLevelName(detailMember.memberLevelId) || '普通会员'}}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账户余额" label-class-name="detail-label">
          <span class="detail-value balance">{{detailMember.balance || 0}} 元</span>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" label-class-name="detail-label">
          <span class="detail-value">{{detailMember.createTime | formatDateTime}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录" label-class-name="detail-label">
          <span class="detail-value">{{detailMember.lastLoginTime | formatDateTime}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态" span="2" label-class-name="detail-label">
          <el-tag :type="detailMember.status === 1 ? 'success' : 'danger'" class="status-tag">
            {{detailMember.status === 1 ? '启用' : '禁用'}}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false" size="small" class="close-btn">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 编辑/添加会员对话框 - 美化 -->
    <el-dialog
      :title="isEdit?'编辑会员':'添加会员'"
      :visible.sync="dialogVisible"
      width="50%"
      class="edit-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="member"
               ref="memberForm"
               label-width="120px"
               size="small"
               class="member-form"
      >
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
  </div>
</template>

<script>
// ... existing code ...
import {fetchList, createMember, updateMember, deleteMember, updateStatus, getMemberInfo} from '@/api/member';
import {fetchAvailableList} from '@/api/memberLevel';
import {formatDate} from '@/utils/date';

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
      memberLevels: []
    }
  },
  watch: {
    // ... existing code ...
    currentPage(newVal) {
      this.listQuery.pageNum = newVal;
    },
    'listQuery.pageNum'(newVal) {
      this.currentPage = newVal;
    }
  },
  created() {
    this.getList();
    this.getMemberLevels();
  },
  filters: {
    formatDateTime(time) {
      if (time == null || time === '') {
        return 'N/A';
      }
      let date = new Date(time);
      return formatDate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  methods: {
    // 新增方法：获取等级标签类型
    getLevelTagType(levelId) {
      const types = ['', 'success', 'warning', 'danger', 'info'];
      return types[levelId % types.length] || 'primary';
    },

    // ... existing code ...
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
      console.log('切换页码前 - 当前页码:', this.listQuery.pageNum, '目标页码:', val, '总条数:', this.total);
      this.listQuery.pageNum = val;
      this.currentPage = val;
      this.getList();
    },
    handlePrevClick(val) {
      console.log('点击前一页 - 当前页码:', val);
      this.listQuery.pageNum = val;
      this.currentPage = val;
      this.getList();
    },
    handleNextClick(val) {
      console.log('点击后一页 - 当前页码:', val);
      this.listQuery.pageNum = val;
      this.currentPage = val;
      this.getList();
    },
    handleAdd() {
      this.dialogVisible = true;
      this.isEdit = false;
      this.member = Object.assign({}, defaultMember);
      if (this.memberLevels && this.memberLevels.length > 0) {
        this.member.memberLevelId = this.memberLevels[0].id;
      }
    },
    handleStatusChange(index, row) {
      this.$confirm('是否要修改该会员状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const updateData = {
          id: row.id,
          status: row.status
        };
        updateMember(updateData).then(response => {
          this.$message({
            type: 'success',
            message: '修改成功!'
          });
        }).catch(error => {
          this.$message({
            type: 'error',
            message: '修改失败!'
          });
          this.getList();
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        });
        this.getList();
      });
    },
    handleDelete(index, row) {
      this.$confirm('是否要删除该会员?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember(row.id).then(response => {
          if (response && response.code === 200) {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          } else {
            this.$message({
              type: 'error',
              message: response && response.message ? response.message : '删除失败!'
            });
          }
        }).catch(error => {
          this.$message({
            type: 'error',
            message: '网络错误，请稍后重试!'
          });
          console.error('删除会员失败:', error);
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消删除'
        });
      });
    },
    handleUpdate(index, row) {
      this.dialogVisible = true;
      this.isEdit = true;
      this.member = Object.assign({}, row);
    },
    handleView(index, row) {
      console.log('尝试获取会员详情，会员ID:', row.id);
      this.detailDialogVisible = true;

      this.$loading({
        lock: true,
        text: '加载中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      getMemberInfo(row.id).then(response => {
        console.log('会员详情接口返回数据:', response);

        if (response && response.data) {
          this.detailMember = response.data;
          console.log('成功设置会员详情数据:', this.detailMember);
        } else {
          this.detailMember = Object.assign({}, defaultMember);
          console.warn('会员详情数据不完整:', response);
          this.$message.warning('获取会员详情数据不完整');
        }
      }).catch(error => {
        console.error('获取会员详情失败:', error);
        this.detailMember = Object.assign({}, defaultMember);
        this.$message.error('获取会员详情失败，请稍后重试');
        this.detailDialogVisible = false;
      }).finally(() => {
        this.$loading().close();
      });
    },
    handleDialogConfirm() {
      this.$confirm('是否要确认?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.isEdit) {
          updateMember(this.member).then(response => {
            this.$message({
              message: '修改成功！',
              type: 'success'
            });
            this.dialogVisible = false;
            this.getList();
          })
        } else {
          createMember(this.member).then(response => {
            this.$message({
              message: '添加成功！',
              type: 'success'
            });
            this.dialogVisible = false;
            this.getList();
          })
        }
      })
    },
    getList() {
      this.listLoading = true;
      this.list = [];

      console.log('开始获取数据 - 当前查询参数:', this.listQuery);
      fetchList(this.listQuery).then(response => {
        this.listLoading = false;

        console.log('API返回的原始数据:', response);

        const allMembers = response.data && Array.isArray(response.data.list) ? response.data.list : [];
        console.log('处理后的数据列表长度:', allMembers.length);

        let filteredMembers = [...allMembers];
        console.log('初始过滤数据量:', filteredMembers.length);

        if (this.listQuery.nickname) {
          filteredMembers = filteredMembers.filter(member =>
            member.nickname && member.nickname.toLowerCase().includes(this.listQuery.nickname.toLowerCase())
          );
          console.log('昵称过滤后的数据量:', filteredMembers.length, '过滤条件:', this.listQuery.nickname);
        }

        if (this.listQuery.memberLevelId) {
          filteredMembers = filteredMembers.filter(member =>
            member.memberLevelId === this.listQuery.memberLevelId
          );
          console.log('等级过滤后的数据量:', filteredMembers.length, '过滤条件:', this.listQuery.memberLevelId);
        }

        this.total = filteredMembers.length;
        console.log('最终设置的总条数:', this.total);

        const {pageNum, pageSize} = this.listQuery;
        const startIndex = (pageNum - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        this.list = filteredMembers.slice(startIndex, endIndex);

        const totalPages = Math.ceil(this.total / pageSize);

        console.log('获取数据完成 - 总条数:', this.total, '当前页码:', pageNum, '每页条数:', pageSize);
        console.log('计算的总页数:', totalPages);

        this.$nextTick(() => {
          if (pageNum > totalPages && totalPages > 0) {
            console.log('页码超出范围，重置为:', totalPages);
            this.listQuery.pageNum = totalPages;
            this.currentPage = totalPages;
            this.getList();
          } else if (totalPages > 1 && pageNum === 1) {
            this.$forceUpdate();
          }
        });
      }).catch(error => {
        this.listLoading = false;
        this.list = [];
        this.total = 0;
        console.error('获取会员列表失败:', error);
      });
    },
    // ... existing code ...
    getMemberLevels() {
      fetchAvailableList({}).then(response => {
        console.log('会员等级接口原始响应:', response);

        if (response && response.data) {
          // 修复：添加会员等级数据处理逻辑
          if (Array.isArray(response.data)) {
            this.memberLevels = response.data;
          } else if (response.data.list && Array.isArray(response.data.list)) {
            this.memberLevels = response.data.list;
          } else {
            this.memberLevels = [];
            console.warn('会员等级数据格式异常:', response.data);
          }
          console.log('成功获取会员等级列表:', this.memberLevels);
        } else {
          this.memberLevels = [];
          console.warn('会员等级接口返回数据为空');
        }
      }).catch(error => {
        console.error('获取会员等级列表失败:', error);
        this.memberLevels = [];
        this.$message.error('获取会员等级列表失败，请检查网络连接');
      });
    },
// ... existing code ...
    getLevelName(levelId) {
      if (!levelId || !this.memberLevels || this.memberLevels.length === 0) return null;
      const level = this.memberLevels.find(item => item.id === levelId);
      return level ? level.name : null;
    }
  }
}
</script>

<style scoped>
/* 美化样式 */
.app-container {
  padding: 20px;
  background: #f5f7fa;
}

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
}

.pagination-container {
  text-align: center;
  margin-top: 20px;
}

.custom-pagination {
  justify-content: center;
}

/* 对话框样式 */
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

/* 响应式设计 */
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
}
</style>
