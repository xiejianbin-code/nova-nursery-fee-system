<template>
  <div class="page-container">
    <div class="table-page">
      <!-- 搜索栏 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="幼儿姓名">
            <el-select v-model="searchForm.childId" placeholder="请选择幼儿" filterable clearable style="width: 200px">
              <el-option
                v-for="item in childOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="合同状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
              <el-option label="生效中" :value="1" />
              <el-option label="已到期" :value="2" />
              <el-option label="已变更" :value="3" />
              <el-option label="已终止" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <div class="table-area">
        <!-- 工具栏 -->
        <div class="table-toolbar">
          <el-button type="primary" @click="handleAdd">新签合同</el-button>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" border stripe>
          <el-table-column prop="contractNo" label="合同编号" width="150" />
          <el-table-column prop="childName" label="幼儿姓名" width="100" />
          <el-table-column prop="contractName" label="合同名称" min-width="150" />
          <el-table-column prop="courseType" label="课程类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.courseType === 'MONTHLY' ? 'primary' : 'success'" size="small">
                {{ row.courseType === 'MONTHLY' ? '按月' : '按学期' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="endDate" label="结束日期" width="120" />
          <el-table-column prop="status" label="合同状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="statusMap[row.status]" size="small">
                {{ statusText[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">查看</el-button>
              <el-button type="success" link @click="handleRenew(row)" v-if="row.status === 1">续签</el-button>
              <el-button type="warning" link @click="handleChange(row)" v-if="row.status === 1">变更</el-button>
              <el-button type="danger" link @click="handleTerminate(row)" v-if="row.status === 1">终止</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-area">
          <Pagination
            v-model:page="pagination.pageNum"
            v-model:limit="pagination.pageSize"
            :total="pagination.total"
            @pagination="handlePagination"
          />
        </div>
      </div>
    </div>

    <!-- 新签合同弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="新签合同"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="幼儿" prop="childId">
              <el-select v-model="form.childId" placeholder="请选择幼儿" filterable style="width: 100%">
                <el-option
                  v-for="item in childOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同名称" prop="contractName">
              <el-input v-model="form.contractName" placeholder="请输入合同名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" placeholder="请选择课程类型" style="width: 100%">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费标准">
              <el-select v-model="form.feeTemplateId" placeholder="选择收费标准模板" style="width: 100%" @change="handleTemplateChange">
                <el-option
                  v-for="item in feeTemplateOptions"
                  :key="item.id"
                  :label="item.templateName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="请选择开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                placeholder="请选择结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 年龄段收费明细 -->
        <el-divider content-position="left">年龄段收费明细</el-divider>
        <el-table :data="form.feeItems" border style="margin-bottom: 20px">
          <el-table-column prop="ageRange" label="年龄段" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.ageRange" placeholder="如：3-4岁" />
            </template>
          </el-table-column>
          <el-table-column prop="educationFee" label="保教费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.educationFee" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column prop="mealFee" label="伙食费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.mealFee" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveFeeItem($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddFeeItem" style="margin-bottom: 20px">添加年龄段</el-button>

        <!-- 其他费用 -->
        <el-divider content-position="left">其他费用</el-divider>
        <el-table :data="form.otherFees" border>
          <el-table-column prop="feeCode" label="费用代码" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeCode" placeholder="费用代码" />
            </template>
          </el-table-column>
          <el-table-column prop="feeName" label="费用名称" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeName" placeholder="费用名称" />
            </template>
          </el-table-column>
          <el-table-column prop="chargeCycle" label="收取周期" min-width="120">
            <template #default="{ row }">
              <el-select v-model="row.chargeCycle" placeholder="收取周期" style="width: 100%">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
                <el-option label="一次性" value="ONCE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="feeStandard" label="收费标准" min-width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.feeStandard" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveOtherFee($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddOtherFee" style="margin-top: 10px">添加其他费用</el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="viewVisible"
      title="合同详情"
      width="900px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ viewData.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="幼儿姓名">{{ viewData.childName }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ viewData.contractName }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">
          <el-tag :type="viewData.courseType === 'MONTHLY' ? 'primary' : 'success'" size="small">
            {{ viewData.courseType === 'MONTHLY' ? '按月' : '按学期' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ viewData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ viewData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="合同状态">
          <el-tag :type="statusMap[viewData.status]" size="small">
            {{ statusText[viewData.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">年龄段收费明细</el-divider>
      <el-table :data="viewData.feeItems" border style="margin-bottom: 20px">
        <el-table-column prop="ageRange" label="年龄段" />
        <el-table-column prop="educationFee" label="保教费" />
        <el-table-column prop="mealFee" label="伙食费" />
      </el-table>

      <el-divider content-position="left">其他费用</el-divider>
      <el-table :data="viewData.otherFees" border>
        <el-table-column prop="feeCode" label="费用代码" />
        <el-table-column prop="feeName" label="费用名称" />
        <el-table-column prop="chargeCycle" label="收取周期">
          <template #default="{ row }">
            {{ getChargeCycleText(row.chargeCycle) }}
          </template>
        </el-table-column>
        <el-table-column prop="feeStandard" label="收费标准" />
      </el-table>
    </el-dialog>

    <!-- 续签合同弹窗 -->
    <el-dialog
      v-model="renewVisible"
      title="续签合同"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form :model="renewForm" :rules="renewRules" ref="renewFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原合同编号">
              <el-input :value="renewForm.originalContractNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="幼儿姓名">
              <el-input :value="renewForm.childName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="新合同名称" prop="contractName">
              <el-input v-model="renewForm.contractName" placeholder="请输入新合同名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费标准">
              <el-select v-model="renewForm.feeTemplateId" placeholder="选择收费标准模板" style="width: 100%" @change="handleRenewTemplateChange">
                <el-option
                  v-for="item in feeTemplateOptions"
                  :key="item.id"
                  :label="item.templateName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="renewForm.startDate"
                type="date"
                placeholder="请选择开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="renewForm.endDate"
                type="date"
                placeholder="请选择结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 年龄段收费明细 -->
        <el-divider content-position="left">年龄段收费明细</el-divider>
        <el-table :data="renewForm.feeItems" border style="margin-bottom: 20px">
          <el-table-column prop="ageRange" label="年龄段" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.ageRange" placeholder="如：3-4岁" />
            </template>
          </el-table-column>
          <el-table-column prop="educationFee" label="保教费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.educationFee" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column prop="mealFee" label="伙食费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.mealFee" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveRenewFeeItem($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddRenewFeeItem" style="margin-bottom: 20px">添加年龄段</el-button>

        <!-- 其他费用 -->
        <el-divider content-position="left">其他费用</el-divider>
        <el-table :data="renewForm.otherFees" border>
          <el-table-column prop="feeCode" label="费用代码" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeCode" placeholder="费用代码" />
            </template>
          </el-table-column>
          <el-table-column prop="feeName" label="费用名称" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeName" placeholder="费用名称" />
            </template>
          </el-table-column>
          <el-table-column prop="chargeCycle" label="收取周期" min-width="120">
            <template #default="{ row }">
              <el-select v-model="row.chargeCycle" placeholder="收取周期" style="width: 100%">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
                <el-option label="一次性" value="ONCE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="feeStandard" label="收费标准" min-width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.feeStandard" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveRenewOtherFee($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddRenewOtherFee" style="margin-top: 10px">添加其他费用</el-button>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" :loading="renewLoading" @click="submitRenew">确定</el-button>
      </template>
    </el-dialog>

    <!-- 变更合同弹窗 -->
    <el-dialog
      v-model="changeVisible"
      title="变更合同"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form :model="changeForm" :rules="changeRules" ref="changeFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原合同编号">
              <el-input :value="changeForm.originalContractNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="幼儿姓名">
              <el-input :value="changeForm.childName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="新合同名称" prop="contractName">
              <el-input v-model="changeForm.contractName" placeholder="请输入新合同名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="changeForm.courseType" placeholder="请选择课程类型" style="width: 100%">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收费标准">
              <el-select v-model="changeForm.feeTemplateId" placeholder="选择收费标准模板" style="width: 100%" @change="handleChangeTemplateChange">
                <el-option
                  v-for="item in feeTemplateOptions"
                  :key="item.id"
                  :label="item.templateName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="changeForm.endDate"
                type="date"
                placeholder="请选择结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="变更原因" prop="changeReason">
              <el-input
                v-model="changeForm.changeReason"
                type="textarea"
                :rows="2"
                placeholder="请输入变更原因"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 年龄段收费明细 -->
        <el-divider content-position="left">年龄段收费明细</el-divider>
        <el-table :data="changeForm.feeItems" border style="margin-bottom: 20px">
          <el-table-column prop="ageRange" label="年龄段" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.ageRange" placeholder="如：3-4岁" />
            </template>
          </el-table-column>
          <el-table-column prop="educationFee" label="保教费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.educationFee" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column prop="mealFee" label="伙食费" min-width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.mealFee" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveChangeFeeItem($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddChangeFeeItem" style="margin-bottom: 20px">添加年龄段</el-button>

        <!-- 其他费用 -->
        <el-divider content-position="left">其他费用</el-divider>
        <el-table :data="changeForm.otherFees" border>
          <el-table-column prop="feeCode" label="费用代码" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeCode" placeholder="费用代码" />
            </template>
          </el-table-column>
          <el-table-column prop="feeName" label="费用名称" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.feeName" placeholder="费用名称" />
            </template>
          </el-table-column>
          <el-table-column prop="chargeCycle" label="收取周期" min-width="120">
            <template #default="{ row }">
              <el-select v-model="row.chargeCycle" placeholder="收取周期" style="width: 100%">
                <el-option label="按月" value="MONTHLY" />
                <el-option label="按学期" value="SEMESTER" />
                <el-option label="一次性" value="ONCE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="feeStandard" label="收费标准" min-width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.feeStandard" :precision="2" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link @click="handleRemoveChangeOtherFee($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="handleAddChangeOtherFee" style="margin-top: 10px">添加其他费用</el-button>
      </el-form>
      <template #footer>
        <el-button @click="changeVisible = false">取消</el-button>
        <el-button type="primary" :loading="changeLoading" @click="submitChange">确定</el-button>
      </template>
    </el-dialog>

    <!-- 终止合同弹窗 -->
    <el-dialog
      v-model="terminateVisible"
      title="终止合同"
      width="400px"
    >
      <el-form :model="terminateForm" label-width="80px">
        <el-form-item label="终止原因">
          <el-input
            v-model="terminateForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入终止原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="terminateVisible = false">取消</el-button>
        <el-button type="primary" :loading="terminateLoading" @click="submitTerminate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/common/Pagination.vue'
import { getContractList, getContractDetail, addContract, terminateContract, getEnabledFeeTemplates, renewContract, changeContract } from '@/api/contract'
import { getChildList } from '@/api/child'
import { getFeeTemplateDetail } from '@/api/feeTemplate'

const statusMap = {
  1: 'success',
  2: 'info',
  3: 'warning',
  4: 'danger'
}

const statusText = {
  1: '生效中',
  2: '已到期',
  3: '已变更',
  4: '已终止'
}

const chargeCycleText = {
  'ONCE': '一次性',
  'MONTHLY': '按月',
  'SEMESTER': '按学期'
}

const getChargeCycleText = (cycle) => {
  return chargeCycleText[cycle] || cycle
}

const loading = ref(false)
const tableData = ref([])
const childOptions = ref([])
const feeTemplateOptions = ref([])

const searchForm = reactive({
  childId: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  childId: null,
  contractName: '',
  courseType: '',
  feeTemplateId: null,
  startDate: '',
  endDate: '',
  feeItems: [],
  otherFees: []
})

const rules = {
  childId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const viewVisible = ref(false)
const viewData = ref({})

const terminateVisible = ref(false)
const terminateLoading = ref(false)
const terminateForm = reactive({
  id: null,
  reason: ''
})

const renewVisible = ref(false)
const renewLoading = ref(false)
const renewFormRef = ref(null)
const renewForm = reactive({
  originalContractId: null,
  originalContractNo: '',
  childName: '',
  contractName: '',
  feeTemplateId: null,
  startDate: '',
  endDate: '',
  feeItems: [],
  otherFees: []
})

const renewRules = {
  contractName: [{ required: true, message: '请输入新合同名称', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const changeVisible = ref(false)
const changeLoading = ref(false)
const changeFormRef = ref(null)
const changeForm = reactive({
  originalContractId: null,
  originalContractNo: '',
  childName: '',
  contractName: '',
  courseType: '',
  feeTemplateId: null,
  endDate: '',
  changeReason: '',
  feeItems: [],
  otherFees: []
})

const changeRules = {
  contractName: [{ required: true, message: '请输入新合同名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  changeReason: [{ required: true, message: '请输入变更原因', trigger: 'blur' }]
}

const fetchChildOptions = async () => {
  try {
    const res = await getChildList({ page: 1, limit: 10000, status: 1 })
    childOptions.value = res.data?.records || []
  } catch (error) {
    console.error('获取幼儿列表失败:', error)
  }
}

const fetchFeeTemplateOptions = async () => {
  try {
    const res = await getEnabledFeeTemplates()
    feeTemplateOptions.value = res.data || []
  } catch (error) {
    console.error('获取收费标准列表失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      childId: searchForm.childId,
      status: searchForm.status
    }
    const res = await getContractList(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取合同列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.childId = null
  searchForm.status = null
  handleSearch()
}

const handlePagination = () => {
  fetchData()
}

const handleAdd = () => {
  Object.assign(form, {
    childId: null,
    contractName: '',
    courseType: '',
    feeTemplateId: null,
    startDate: '',
    endDate: '',
    feeItems: [{ ageRange: '', educationFee: 0, mealFee: 0 }],
    otherFees: []
  })
  dialogVisible.value = true
}

const handleTemplateChange = async (templateId) => {
  if (!templateId) return
  try {
    const res = await getFeeTemplateDetail(templateId)
    form.feeItems = res.data?.items || []
    form.otherFees = res.data?.otherFees || []
    form.courseType = res.data?.courseType || ''
  } catch (error) {
    console.error('获取模板详情失败:', error)
  }
}

const handleAddFeeItem = () => {
  form.feeItems.push({ ageRange: '', educationFee: 0, mealFee: 0 })
}

const handleRemoveFeeItem = (index) => {
  form.feeItems.splice(index, 1)
}

const handleAddOtherFee = () => {
  form.otherFees.push({ feeCode: '', feeName: '', chargeCycle: '', feeStandard: 0 })
}

const handleRemoveOtherFee = (index) => {
  form.otherFees.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.feeItems.length === 0) {
        ElMessage.warning('请至少添加一个年龄段收费明细')
        return
      }
      
      submitLoading.value = true
      try {
        await addContract(form)
        ElMessage.success('新签合同成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleView = async (row) => {
  try {
    const res = await getContractDetail(row.id)
    viewData.value = res.data
    viewVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleRenew = async (row) => {
  try {
    const res = await getContractDetail(row.id)
    const detail = res.data
    Object.assign(renewForm, {
      originalContractId: row.id,
      originalContractNo: detail.contractNo,
      childName: detail.childName,
      contractName: detail.contractName + '(续签)',
      feeTemplateId: null,
      startDate: '',
      endDate: '',
      feeItems: detail.feeItems ? JSON.parse(JSON.stringify(detail.feeItems)) : [{ ageRange: '', educationFee: 0, mealFee: 0 }],
      otherFees: detail.otherFees ? JSON.parse(JSON.stringify(detail.otherFees)) : []
    })
    renewVisible.value = true
  } catch (error) {
    console.error('获取合同详情失败:', error)
  }
}

const handleRenewTemplateChange = async (templateId) => {
  if (!templateId) return
  try {
    const res = await getFeeTemplateDetail(templateId)
    renewForm.feeItems = res.data?.items || []
    renewForm.otherFees = res.data?.otherFees || []
  } catch (error) {
    console.error('获取模板详情失败:', error)
  }
}

const handleAddRenewFeeItem = () => {
  renewForm.feeItems.push({ ageRange: '', educationFee: 0, mealFee: 0 })
}

const handleRemoveRenewFeeItem = (index) => {
  renewForm.feeItems.splice(index, 1)
}

const handleAddRenewOtherFee = () => {
  renewForm.otherFees.push({ feeCode: '', feeName: '', chargeCycle: '', feeStandard: 0 })
}

const handleRemoveRenewOtherFee = (index) => {
  renewForm.otherFees.splice(index, 1)
}

const submitRenew = async () => {
  if (!renewFormRef.value) return
  await renewFormRef.value.validate(async (valid) => {
    if (valid) {
      if (renewForm.feeItems.length === 0) {
        ElMessage.warning('请至少添加一个年龄段收费明细')
        return
      }
      
      renewLoading.value = true
      try {
        await renewContract({
          originalContractId: renewForm.originalContractId,
          contractName: renewForm.contractName,
          startDate: renewForm.startDate,
          endDate: renewForm.endDate,
          feeItems: renewForm.feeItems,
          otherFees: renewForm.otherFees
        })
        ElMessage.success('续签合同成功')
        renewVisible.value = false
        fetchData()
      } catch (error) {
        console.error('续签失败:', error)
      } finally {
        renewLoading.value = false
      }
    }
  })
}

const handleChange = async (row) => {
  try {
    const res = await getContractDetail(row.id)
    const detail = res.data
    Object.assign(changeForm, {
      originalContractId: row.id,
      originalContractNo: detail.contractNo,
      childName: detail.childName,
      contractName: detail.contractName + '(变更)',
      courseType: detail.courseType,
      feeTemplateId: null,
      endDate: '',
      changeReason: '',
      feeItems: detail.feeItems ? JSON.parse(JSON.stringify(detail.feeItems)) : [{ ageRange: '', educationFee: 0, mealFee: 0 }],
      otherFees: detail.otherFees ? JSON.parse(JSON.stringify(detail.otherFees)) : []
    })
    changeVisible.value = true
  } catch (error) {
    console.error('获取合同详情失败:', error)
  }
}

const handleChangeTemplateChange = async (templateId) => {
  if (!templateId) return
  try {
    const res = await getFeeTemplateDetail(templateId)
    changeForm.feeItems = res.data?.items || []
    changeForm.otherFees = res.data?.otherFees || []
    changeForm.courseType = res.data?.courseType || ''
  } catch (error) {
    console.error('获取模板详情失败:', error)
  }
}

const handleAddChangeFeeItem = () => {
  changeForm.feeItems.push({ ageRange: '', educationFee: 0, mealFee: 0 })
}

const handleRemoveChangeFeeItem = (index) => {
  changeForm.feeItems.splice(index, 1)
}

const handleAddChangeOtherFee = () => {
  changeForm.otherFees.push({ feeCode: '', feeName: '', chargeCycle: '', feeStandard: 0 })
}

const handleRemoveChangeOtherFee = (index) => {
  changeForm.otherFees.splice(index, 1)
}

const submitChange = async () => {
  if (!changeFormRef.value) return
  await changeFormRef.value.validate(async (valid) => {
    if (valid) {
      if (changeForm.feeItems.length === 0) {
        ElMessage.warning('请至少添加一个年龄段收费明细')
        return
      }
      
      changeLoading.value = true
      try {
        await changeContract({
          originalContractId: changeForm.originalContractId,
          contractName: changeForm.contractName,
          courseType: changeForm.courseType,
          endDate: changeForm.endDate,
          changeReason: changeForm.changeReason,
          feeItems: changeForm.feeItems,
          otherFees: changeForm.otherFees
        })
        ElMessage.success('变更合同成功')
        changeVisible.value = false
        fetchData()
      } catch (error) {
        console.error('变更失败:', error)
      } finally {
        changeLoading.value = false
      }
    }
  })
}

const handleTerminate = (row) => {
  terminateForm.id = row.id
  terminateForm.reason = ''
  terminateVisible.value = true
}

const submitTerminate = async () => {
  if (!terminateForm.reason) {
    ElMessage.warning('请输入终止原因')
    return
  }
  
  terminateLoading.value = true
  try {
    await terminateContract(terminateForm.id, terminateForm.reason)
    ElMessage.success('合同已终止')
    terminateVisible.value = false
    fetchData()
  } catch (error) {
    console.error('终止失败:', error)
  } finally {
    terminateLoading.value = false
  }
}

onMounted(() => {
  fetchChildOptions()
  fetchFeeTemplateOptions()
  fetchData()
})
</script>

<style lang="scss" scoped>
.table-toolbar {
  margin-bottom: 16px;
}
</style>
