<template>
  <div class="app-container member-level-container">
    <!-- 筛选搜索区域 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-header">
        <i class="el-icon-search filter-icon"></i>
        <span class="filter-title">筛选搜索</span>
        <div class="filter-actions">
          <el-button
            type="primary"
            @click="handleSearchList()"
            size="small"
            class="search-btn">
            <i class="el-icon-search"></i>查询搜索
          </el-button>
          <el-button
            @click="handleResetSearch()"
            size="small"
            class="reset-btn">
            <i class="el-icon-refresh"></i>重置
          </el-button>
        </div>
      </div>
      <div class="filter-content">
        <el-form :inline="true" :model="listQuery" label-width="auto" class="filter-form">
          <el-form-item label="等级名称：" class="filter-item">
            <el-select
              v-model="listQuery.name"
              placeholder="请选择等级名称"
              clearable
              class="filter-select"
              style="width: 200px">
              <el-option v-for="option in levelNameOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否启用：" class="filter-item">
            <el-select
              v-model="listQuery.defaultStatus"
              placeholder="请选择状态"
              clearable
              class="filter-select"
              style="width: 150px">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="operate-card" shadow="hover">
      <div class="operate-header">
        <i class="el-icon-tickets operate-icon"></i>
        <span class="operate-title">会员等级列表</span>
        <el-button
          size="mini"
          class="btn-add"
          @click="handleAdd()"
          type="primary">
          <i class="el-icon-plus"></i>添加会员等级
        </el-button>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <div class="table-card-container">
      <el-card shadow="hover" class="table-card">
        <el-table
          ref="memberLevelTable"
          :data="list"
          style="width: 100%;"
          v-loading="listLoading"
          border
          stripe
          class="member-level-table"
          :header-cell-style="{background: '#f5f7fa', color: '#606266', fontWeight: '600'}">
          <el-table-column label="等级编号" width="100" align="center" prop="id">
            <template slot-scope="scope">
              <span class="level-id">{{scope.row.id}}</span>
            </template>
          </el-table-column>
          <el-table-column label="等级名称" align="center" prop="name" min-width="120">
            <template slot-scope="scope">
              <span class="level-name">{{scope.row.name}}</span>
            </template>
          </el-table-column>
          <el-table-column label="成长值下限" align="center" prop="growthPoint" width="120">
            <template slot-scope="scope">
              <el-tag type="info" size="small">{{scope.row.growthPoint}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="免运费标准" align="center" prop="freeFreightPoint" width="130">
            <template slot-scope="scope">
              <span class="free-freight">{{scope.row.freeFreightPoint}} 元</span>
            </template>
          </el-table-column>
          <el-table-column label="折扣" align="center" prop="defaultDiscount" width="100">
            <template slot-scope="scope">
              <el-tag type="success" size="small">{{scope.row.defaultDiscount}}%</el-tag>
            </template>
          </el-table-column>
          <!-- 新增特权信息列 -->
          <el-table-column label="评论成长值" align="center" width="120" prop="commentGrowthPoint">
            <template slot-scope="scope">
              <span class="comment-growth">{{scope.row.commentGrowthPoint || 0}}</span>
            </template>
          </el-table-column>
          <el-table-column label="特权" align="center" width="200" prop="privileges">
            <template slot-scope="scope">
              <div class="privilege-tags">
                <el-tag v-if="scope.row.priviledgeFreeFreight" size="mini" type="success" class="privilege-tag">免邮</el-tag>
                <el-tag v-if="scope.row.priviledgeSignIn" size="mini" type="primary" class="privilege-tag">签到</el-tag>
                <el-tag v-if="scope.row.priviledgeComment" size="mini" type="warning" class="privilege-tag">评论奖励</el-tag>
                <el-tag v-if="scope.row.priviledgePromotion" size="mini" type="info" class="privilege-tag">专享活动</el-tag>
                <el-tag v-if="scope.row.priviledgeMemberPrice" size="mini" type="danger" class="privilege-tag">会员价</el-tag>
                <el-tag v-if="scope.row.priviledgeBirthday" size="mini" class="privilege-tag">生日特权</el-tag>
                <span v-if="!hasPrivileges(scope.row)" class="no-privilege">无特权</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center" prop="defaultStatus">
            <template slot-scope="scope">
              <el-switch
                @change="handleStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.defaultStatus"
                active-color="#13ce66"
                inactive-color="#ff4949"
                class="status-switch">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                @click="handleUpdate(scope.$index, scope.row)"
                class="action-btn edit-btn">
                <i class="el-icon-edit"></i>编辑
              </el-button>
              <el-button
                size="mini"
                type="text"
                @click="handleDelete(scope.$index, scope.row)"
                class="action-btn delete-btn">
                <i class="el-icon-delete"></i>删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
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
        class="custom-pagination">
      </el-pagination>
    </div>

    <!-- 编辑/添加会员等级对话框 -->
    <el-dialog
      :title="isEdit?'编辑会员等级':'添加会员等级'"
      :visible.sync="dialogVisible"
      width="700px"
      class="member-level-dialog"
      :close-on-click-modal="false">
      <div class="dialog-content">
        <el-form
          :model="memberLevel"
          ref="memberLevelForm"
          label-width="120px"
          size="small"
          class="dialog-form">
          <el-form-item label="等级名称：" prop="name" class="form-item">
            <el-input
              v-model="memberLevel.name"
              placeholder="请输入等级名称"
              class="form-input">
            </el-input>
          </el-form-item>
          <el-form-item label="成长值下限：" prop="growthPoint" class="form-item">
            <el-input
              v-model="memberLevel.growthPoint"
              type="number"
              placeholder="请输入成长值下限"
              class="form-input">
            </el-input>
          </el-form-item>
          <el-form-item label="免运费标准：" prop="freeFreightPoint" class="form-item">
            <el-input
              v-model="memberLevel.freeFreightPoint"
              type="number"
              placeholder="请输入免运费标准"
              class="form-input">
              <template slot="append">元</template>
            </el-input>
          </el-form-item>
          <el-form-item label="默认折扣：" prop="defaultDiscount" class="form-item">
            <el-input
              v-model="memberLevel.defaultDiscount"
              type="number"
              placeholder="请输入默认折扣"
              class="form-input">
              <template slot="append">%</template>
            </el-input>
          </el-form-item>
          <!-- 新增特权相关字段 -->
          <el-form-item label="评论赠送成长值：" prop="commentGrowthPoint" class="form-item">
            <el-input
              v-model="memberLevel.commentGrowthPoint"
              type="number"
              placeholder="请输入评论赠送成长值"
              class="form-input">
            </el-input>
          </el-form-item>
          <el-form-item label="特权设置：" prop="privileges" class="form-item privilege-item">
            <el-checkbox-group v-model="privilegeList" class="privilege-checkbox-group">
              <el-checkbox label="priviledgeFreeFreight" class="privilege-checkbox">免邮特权</el-checkbox>
              <el-checkbox label="priviledgeSignIn" class="privilege-checkbox">签到特权</el-checkbox>
              <el-checkbox label="priviledgeComment" class="privilege-checkbox">评论获奖励特权</el-checkbox>
              <el-checkbox label="priviledgePromotion" class="privilege-checkbox">专享活动特权</el-checkbox>
              <el-checkbox label="priviledgeMemberPrice" class="privilege-checkbox">会员价格特权</el-checkbox>
              <el-checkbox label="priviledgeBirthday" class="privilege-checkbox">生日特权</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="是否启用：" prop="defaultStatus" class="form-item">
            <el-radio-group v-model="memberLevel.defaultStatus" class="status-radio-group">
              <el-radio :label="1" class="status-radio">启用</el-radio>
              <el-radio :label="0" class="status-radio">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          @click="dialogVisible = false"
          size="small"
          class="cancel-btn">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleDialogConfirm()"
          size="small"
          class="confirm-btn">
          确定
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// ... existing code ...
import {fetchList, createMemberLevel, updateMemberLevel, deleteMemberLevel, updateMemberLevelStatus} from '@/api/memberLevel';
import {formatDate} from '@/utils/date';

const defaultListQuery = {
  pageNum: 1,
  pageSize: 10,
  name: null,
  status: null
};

const defaultMemberLevel = {
  id: null,
  name: null,
  growthPoint: 0,
  defaultDiscount: 100,
  freeFreightPoint: 0,
  defaultStatus: 0,
  status: 0,
  commentGrowthPoint: 0,
  priviledgeFreeFreight: 0,
  priviledgeSignIn: 0,
  priviledgeComment: 0,
  priviledgePromotion: 0,
  priviledgeMemberPrice: 0,
  priviledgeBirthday: 0
};

const validateRules = {
  name: [
    { required: true, message: '请输入等级名称', trigger: 'blur' },
    { min: 1, max: 20, message: '等级名称长度在1到20个字符', trigger: 'blur' }
  ],
  growthPoint: [
    { required: true, message: '请输入成长值下限', trigger: 'blur' },
    { type: 'number', message: '成长值必须为数字', trigger: 'blur' },
    { min: 0, message: '成长值不能小于0', trigger: 'blur' }
  ],
  freeFreightPoint: [
    { type: 'number', message: '免运费标准必须为数字', trigger: 'blur' },
    { min: 0, message: '免运费标准不能小于0', trigger: 'blur' }
  ],
  defaultDiscount: [
    { required: true, message: '请输入默认折扣', trigger: 'blur' },
    { type: 'number', message: '默认折扣必须为数字', trigger: 'blur' },
    { min: 1, max: 100, message: '默认折扣必须在1到100之间', trigger: 'blur' }
  ],
  commentGrowthPoint: [
    { type: 'number', message: '评论成长值必须为数字', trigger: 'blur' },
    { min: 0, message: '评论成长值不能小于0', trigger: 'blur' }
  ]
};

export default {
  name: 'memberLevelList',
  data() {
    return {
      listQuery: Object.assign({}, defaultListQuery),
      list: [],
      total: 0,
      currentPage: 1,
      listLoading: false,
      dialogVisible: false,
      memberLevel: Object.assign({}, defaultMemberLevel),
      isEdit: false,
      levelNameOptions: [],
      validateRules: validateRules,
      privilegeList: []
    }
  },
  watch: {
    currentPage(newVal) {
      this.listQuery.pageNum = newVal;
    },
    'listQuery.pageNum'(newVal) {
      this.currentPage = newVal;
    }
  },
  created() {
    this.getList();
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
    hasPrivileges(row) {
      return row.priviledgeFreeFreight || row.priviledgeSignIn || row.priviledgeComment ||
        row.priviledgePromotion || row.priviledgeMemberPrice || row.priviledgeBirthday;
    },
    handleAdd() {
      this.dialogVisible = true;
      this.isEdit = false;
      this.memberLevel = Object.assign({}, defaultMemberLevel);
      this.privilegeList = [];
    },
    handleUpdate(index, row) {
      this.dialogVisible = true;
      this.isEdit = true;
      this.memberLevel = Object.assign({}, row);
      // 设置特权复选框状态
      this.privilegeList = [];
      if (row.priviledgeFreeFreight) this.privilegeList.push('priviledgeFreeFreight');
      if (row.priviledgeSignIn) this.privilegeList.push('priviledgeSignIn');
      if (row.priviledgeComment) this.privilegeList.push('priviledgeComment');
      if (row.priviledgePromotion) this.privilegeList.push('priviledgePromotion');
      if (row.priviledgeMemberPrice) this.privilegeList.push('priviledgeMemberPrice');
      if (row.priviledgeBirthday) this.privilegeList.push('priviledgeBirthday');
    },
    handleDelete(index, row) {
      this.$confirm('是否要删除该会员等级?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberLevel(row.id).then(response => {
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
          console.error('删除会员等级失败:', error);
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消删除'
        });
      });
    },
    handleStatusChange(index, row) {
      const originalStatus = row.defaultStatus;
      const newStatus = originalStatus === 0 ? 1 : 0;
      console.log('旧状态:', originalStatus);
      console.log('新状态:', newStatus);

      this.$confirm('是否要修改该会员等级状态?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateMemberLevelStatus(row.id, originalStatus).then(response => {
          if (response && response.code === 200) {
            this.$message({
              type: 'success',
              message: '修改成功!'
            });
            this.getList();
          } else {
            this.$message({
              type: 'error',
              message: response && response.message ? response.message : '修改失败!'
            });
            row.defaultStatus = originalStatus;
            this.$forceUpdate();
          }
        }).catch(error => {
          console.error('修改状态失败:', error);
          this.$message({
            type: 'error',
            message: '修改失败!'
          });
          row.defaultStatus = originalStatus;
          this.$forceUpdate();
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消修改'
        });
        row.defaultStatus = originalStatus;
        this.$forceUpdate();
      });
    },
    handleDialogConfirm() {
      // 在提交前设置特权字段值
      this.memberLevel.priviledgeFreeFreight = this.privilegeList.includes('priviledgeFreeFreight') ? 1 : 0;
      this.memberLevel.priviledgeSignIn = this.privilegeList.includes('priviledgeSignIn') ? 1 : 0;
      this.memberLevel.priviledgeComment = this.privilegeList.includes('priviledgeComment') ? 1 : 0;
      this.memberLevel.priviledgePromotion = this.privilegeList.includes('priviledgePromotion') ? 1 : 0;
      this.memberLevel.priviledgeMemberPrice = this.privilegeList.includes('priviledgeMemberPrice') ? 1 : 0;
      this.memberLevel.priviledgeBirthday = this.privilegeList.includes('priviledgeBirthday') ? 1 : 0;

      this.$refs.memberLevelForm.validate((valid) => {
        if (valid) {
          const memberLevelData = Object.assign({}, this.memberLevel);

          let apiMethod;
          if (this.isEdit) {
            apiMethod = updateMemberLevel(memberLevelData);
          } else {
            const createData = {...memberLevelData};
            delete createData.id;
            apiMethod = createMemberLevel(createData);
          }

          apiMethod.then(response => {
            if (response && response.code === 200) {
              this.$message({
                type: 'success',
                message: this.isEdit ? '修改成功!' : '添加成功!'
              });
              this.dialogVisible = false;
              this.getList();
            } else {
              this.$message({
                type: 'error',
                message: response && response.message ? response.message : (this.isEdit ? '修改失败!' : '添加失败!')
              });
            }
          }).catch(error => {
            console.error(this.isEdit ? '修改会员等级失败:' : '添加会员等级失败:', error);
            this.$message({
              type: 'error',
              message: '网络错误，请稍后重试!'
            });
          });
        } else {
          this.$message({
            type: 'warning',
            message: '表单验证失败，请检查输入!'
          });
          return false;
        }
      });
    },
    getList() {
      this.listLoading = true;
      this.list = [];

      fetchList(this.listQuery).then(response => {
        this.listLoading = false;

        const allMemberLevels = response.data && Array.isArray(response.data) ? response.data :
          (response.data && Array.isArray(response.data.list) ? response.data.list : []);

        const levelNames = [...new Set(allMemberLevels.map(level => level.name).filter(Boolean))];
        this.levelNameOptions = levelNames.map(name => ({ label: name, value: name }));

        let filteredLevels = [...allMemberLevels];

        if (this.listQuery.name) {
          filteredLevels = filteredLevels.filter(level =>
            level.name && level.name.toLowerCase().includes(this.listQuery.name.toLowerCase())
          );
        }

        if (this.listQuery.status !== null && this.listQuery.status !== undefined) {
          filteredLevels = filteredLevels.filter(level =>
            level.status === this.listQuery.status
          );
        }

        this.total = filteredLevels.length;

        const {pageNum, pageSize} = this.listQuery;
        const startIndex = (pageNum - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        this.list = filteredLevels.slice(startIndex, endIndex);

        const totalPages = Math.ceil(this.total / pageSize);

        this.$nextTick(() => {
          if (pageNum > totalPages && totalPages > 0) {
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
        console.error('获取会员等级列表失败:', error);
        this.$message.error('获取会员等级列表失败，请稍后重试');
      });
    }
  }
}
</script>

<style scoped>
.member-level-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 84px);
}

/* 筛选卡片样式 */
.filter-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.filter-header {
  display: flex;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
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
  flex: 1;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.search-btn {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
  border-radius: 6px;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.reset-btn {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background-color: #f5f7fa;
  transform: translateY(-1px);
}

.filter-content {
  padding-top: 15px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.filter-item {
  margin-bottom: 0;
}

.filter-select {
  border-radius: 6px;
}

/* 操作卡片样式 */
.operate-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.operate-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
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
  flex: 1;
}

.btn-add {
  background: linear-gradient(45deg, #67C23A, #85ce61);
  border: none;
  border-radius: 6px;
  color: white;
  box-shadow: 0 2px 6px rgba(103, 194, 58, 0.3);
  transition: all 0.3s ease;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

/* 表格卡片样式 */
.table-card-container {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 12px;
  border: none;
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.member-level-table {
  border-radius: 8px;
  overflow: hidden;
}

.member-level-table >>> .el-table__body tr:hover > td {
  background: linear-gradient(90deg, #f0f9ff 0%, #e6f7ff 100%) !important;
  transform: scale(1.02);
  transition: all 0.3s ease;
}

.level-id {
  font-weight: 600;
  color: #409EFF;
}

.level-name {
  font-weight: 500;
  color: #303133;
}

.free-freight {
  color: #E6A23C;
  font-weight: 500;
}

.comment-growth {
  color: #909399;
  font-weight: 500;
}

.privilege-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
  align-items: center;
}

.privilege-tag {
  margin: 2px;
  border-radius: 4px;
  font-weight: 500;
}

.no-privilege {
  color: #C0C4CC;
  font-size: 12px;
  font-style: italic;
}

.status-switch {
  transform: scale(1.1);
}

.action-btn {
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.edit-btn {
  color: #409EFF;
}

.edit-btn:hover {
  background: linear-gradient(90deg, #ecf5ff 0%, #d9ecff 100%);
  color: #66b1ff;
  transform: translateX(2px);
}

.delete-btn {
  color: #F56C6C;
}

.delete-btn:hover {
  background: linear-gradient(90deg, #fef0f0 0%, #fde2e2 100%);
  color: #f78989;
  transform: translateX(2px);
}

/* 分页样式 */
.pagination-container {
  height: 100px;
  margin-top: 50px;
  text-align: center;
}

.custom-pagination {
  display: inline-block;
}

.custom-pagination >>> .el-pagination.is-background .btn-prev,
.custom-pagination >>> .el-pagination.is-background .btn-next,
.custom-pagination >>> .el-pagination.is-background .el-pager li {
  border-radius: 6px;
  margin: 0 4px;
  transition: all 0.3s ease;
}

.custom-pagination >>> .el-pagination.is-background .btn-prev:hover,
.custom-pagination >>> .el-pagination.is-background .btn-next:hover,
.custom-pagination >>> .el-pagination.is-background .el-pager li:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 对话框样式 */
.member-level-dialog {
  border-radius: 12px;
}

.member-level-dialog >>> .el-dialog {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
}

.member-level-dialog >>> .el-dialog__header {
  background: linear-gradient(90deg, #409EFF 0%, #66b1ff 100%);
  border-radius: 12px 12px 0 0;
  padding: 20px;
}

.member-level-dialog >>> .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.dialog-content {
  padding: 20px;
}

.dialog-form {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}

.dialog-form::-webkit-scrollbar {
  width: 6px;
}

.dialog-form::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.dialog-form::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.dialog-form::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.form-item {
  margin-bottom: 20px;
}

.form-input {
  border-radius: 6px;
}

.form-input >>> .el-input__inner {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.form-input >>> .el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.privilege-item {
  margin-bottom: 10px;
}

.privilege-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.privilege-checkbox {
  margin-right: 0;
}

.status-radio-group {
  display: flex;
  gap: 20px;
}

.status-radio {
  margin-right: 0;
}

.dialog-footer {
  padding: 20px;
  border-top: 1px solid #ebeef5;
  text-align: right;
}

.cancel-btn {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background-color: #f5f7fa;
  transform: translateY(-1px);
}

.confirm-btn {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
  border-radius: 6px;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .member-level-container {
    padding: 10px;
  }

  .filter-form {
    flex-direction: column;
    gap: 15px;
  }

  .privilege-checkbox-group {
    flex-direction: column;
    gap: 10px;
  }

  .operate-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
