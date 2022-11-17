<template>
  <div>
    <el-form ref="form" :model="form" label-width="100px">
      <el-row :gutter="8">
        <el-col :span="12">
          <el-form-item label="样例数据：">
            <el-input v-model="form.sampleData" placeholder="入参样例数据" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-button>下载</el-button>
        </el-col>
      </el-row>
      <el-row :gutter="8">
        <el-col :span="12">
          <el-form-item label="请输入数据：">
            <el-input v-model="form.uploadData">
              <template slot="append">选择文件</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-button>上传</el-button> <span class="red">说明：按照上述样例数据进行入参数据整理。</span>
        </el-col>
      </el-row>
    </el-form>
    <div class="button-section">
      <el-button type="primary" @click="onHandleCompute">运算</el-button>
      <el-button type="primary" @click="onHandleCancel">取消</el-button>
    </div>
    <div class="seperate" />
    <div class="result-section">
      <div class="title">成果输出：</div>
      <div class="content">全球海面地形成果数据</div>
    </div>
    <div class="button-section">
      <el-button type="primary" @click="onHandleDownload">下载</el-button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        sampleData: '',
        uploadData: ''
      }
    }
  },
  methods: {
    // 运算
    onHandleCompute() {
      this.$message.warning('建设中')
    },
    // 取消
    onHandleCancel() {
      this.$message.warning('建设中')
    },
    // 下载
    onHandleDownload() {
      this.$message.warning('建设中')
    }
  }
}
</script>

<style lang="scss" scoped>
.red{
  margin-left: 10px;
  color: #DD180F;
  font-size: 14px;
}
.seperate{
  width: 100%;
  height: 1px;
  background: #D6D6D6;
  margin: 20px 0;
}
.button-section{
  padding: 20px 0;
  text-align: right;
}
.result-section{
  .title{
    font-size: 16px;
    color: #313131;
    margin-bottom: 10px;
  }
  .content{
    line-height: 24px;
    font-size: 14px;
    color: #0454CF;
  }
}
</style>
