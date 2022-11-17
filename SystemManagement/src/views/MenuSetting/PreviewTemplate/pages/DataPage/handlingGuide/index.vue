<template>
  <div class="guide-section">
    <div class="info-section">
      <span class="info-item"><span class="label">更新时间</span>：{{ info.updateTime }}</span>
      <span class="info-item"><span class="label">提供部门：</span>{{ info.provideDepartment }}</span>
      <span class="info-item"><span class="label">浏览次数：</span>{{ info.viewTimes }}</span>
      <span class="info-item"><span class="label">开放级别：</span>{{ info.isOpen }}</span>
    </div>
    <div class="button-section">
      <button v-for="item in buttons" :key="item.value" class="button-item" :class="{'button-item-active': activeButton === item.value}" @click="onHandleClickButton(item)">{{ item.label }}</button>
    </div>
    <div class="content-section">
      <BaseInfo v-if="activeButton === 'base'" />
      <HandlingFlow v-if="activeButton === 'flow'" />
      <HandlingMaterialList v-if="activeButton === 'list'" />
      <HandlingCondition v-if="activeButton === 'condition'" />
      <CommonProblem v-if="activeButton === 'problem'" />
      <RelativeCase v-if="activeButton === 'case'" />
    </div>
  </div>
</template>

<script>
import BaseInfo from './BaseInfo.vue'
import HandlingFlow from './HandlingFlow.vue'
import HandlingMaterialList from './HandlingMaterialList.vue'
import HandlingCondition from './HandlingCondition.vue'
import CommonProblem from './CommonProblem.vue'
import RelativeCase from './RelativeCase.vue'
export default {
  components: { BaseInfo, HandlingFlow, HandlingMaterialList, HandlingCondition, CommonProblem, RelativeCase },
  data() {
    return {
      info: {
        updateTime: '2021-08-4',
        provideDepartment: '测绘及地理空间中心',
        viewTimes: '5200次',
        isOpen: '开放'
      },
      activeButton: 'base',
      buttons: [
        {
          label: '基本信息',
          value: 'base'
        },
        {
          label: '办理流程',
          value: 'flow'
        },
        {
          label: '办理材料目录',
          value: 'list'
        },
        {
          label: '受理条件',
          value: 'condition'
        },
        {
          label: '常见问题',
          value: 'problem'
        },
        {
          label: '相关案例',
          value: 'case'
        }
      ]
    }
  },
  methods: {
    // 办理指南按钮点击
    onHandleClickButton(data) {
      this.activeButton = data.value
    }
  }
}
</script>

<style lang="scss" scoped>
.info-section{
  height: 50px;
  line-height: 50px;
  color: #434343;
  font-size: 16px;
  .info-item{
    margin-right: 40px;
    .label{
      font-weight: bold;
    }
  }
}
.button-section{
  padding: 10px 0;
  .button-item{
    width: 130px;
    height: 32px;
    line-height: 32px;
    color: #2C2C2C;
    background: #E3E3E3;
    border: none;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    margin-right: 20px;
    &-active{
      color: #FFFFFF;
      background: #0454CF;
    }
  }
}
</style>
