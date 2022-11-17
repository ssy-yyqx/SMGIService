<template>
  <div>
    <el-tabs v-if="form.moduleType && form.templateType!==6" v-model="activeName" type="card">
      <el-tab-pane label="基本配置" name="base" />
      <el-tab-pane label="内容配置" name="content" />
    </el-tabs>
    <el-form v-if="activeName==='base'" ref="form" :model="form" label-width="120px" class="config-container">
      <el-form-item label="栏目名称">
        <el-input v-model="form.moduleName" />
      </el-form-item>
      <el-form-item label="模型">
        <el-select v-model="form.templateType" placeholder="请选择活动区域">
          <el-option v-for="(item, index) in templateTypeList" :key="index" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="路由">
        <el-input v-model="form.router" />
      </el-form-item>
      <el-form-item label="图标">
        <el-input v-model="form.icon" />
      </el-form-item>
      <el-form-item label="顺序">
        <el-input v-model="form.sort" />
      </el-form-item>
      <el-form-item label="是否显示">
        <el-switch v-model="form.isShow" />
      </el-form-item>
      <el-form-item label="是否是外部链接">
        <el-switch v-model="form.isLink" />
      </el-form-item>
      <el-form-item v-if="form.isLink" label="外部链接地址">
        <el-input v-model="form.linkUrl" />
      </el-form-item>
      <el-form-item label="是否是叶子节点">
        <el-switch v-model="form.moduleType" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onHandleSubmit">提交</el-button>
        <el-button @click="onHandleCancel">返回列表</el-button>
      </el-form-item>
    </el-form>
    <div v-if="activeName==='content'">
      <component :is="componentId" :id="moduleId" />
    </div>
  </div>
</template>

<script>
import * as API from '@/api/MenuSetting'
import ListAndDetail from '../ConfigTemplate/ListAndDetail'
import SinglePage from '../ConfigTemplate/SinglePage'
import ServicePage from '../ConfigTemplate/ServicePage'
import SpecialService from '../ConfigTemplate/SpecialService'
import { TEMPLATE_DICT, TEMPLATE_COMPONENT } from '../dict'
export default {
  components: { ListAndDetail, SinglePage, ServicePage, SpecialService },
  data() {
    return {
      activeName: 'base',
      moduleId: '',
      form: {},
      templateTypeList: TEMPLATE_DICT
    }
  },
  created() {
    const formData = this.$route.query
    const moduleType = String(formData.moduleType) === '2'
    const isLink = String(formData.isLink) === '1'
    this.form = Object.assign({}, formData, { moduleType, isLink })
    // 获取对应的模板
    if (this.form.moduleType) {
      this.componentId = TEMPLATE_COMPONENT[this.form.templateType]
      this.moduleId = this.form.id
    }
  },
  methods: {
    // 提交
    onHandleSubmit() {
      const isShow = this.form.isShow ? 1 : 2
      const moduleType = this.form.moduleType ? 2 : 1
      const isLink = this.form.isLink ? 1 : 2
      const data = Object.assign({}, this.form, { isShow, moduleType, isLink })
      delete data.childrenList
      API.editMenu(data).then(res => {
        const { code, msg } = res
        if (String(code) === '200') {
          this.$message.success('修改成功！')
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 返回列表
    onHandleCancel() {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="scss" scoped>
.config-container{
  width: 80%;
}
</style>
