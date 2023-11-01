<template>
  <el-tabs
    v-model="activeName"
    :type="option.type || defaultOption.type"
    :tab-position="option.tabPosition || defaultOption.tabPosition"
    :stretch="option.stretch || defaultOption.stretch"
    @tab-click="handleClick"
  >
    <el-tab-pane
      v-for="item in option.column"
      :key="item.name"
      :label="item.label"
      :disabled="item.disabled"
      :name="item.name"
      :lazy="item.lazy"
    >
      <el-form
        :model="forms[item.name]"
        :ref="formRef || 'ruleForm'"
        class="demo-dynamic"
        size='mini'
      >
        <div v-if="formItem.column && formItem.column[item.name] && formItem.column[item.name].length > 0">
          <el-form-item
            v-for="it in formItem.column[item.name]"
            :key="it.prop"
            :label="it.label"
            :prop='it.prop'
            :rules='it.rules'
            v-model="forms[item.name][it.prop]"
            :label-width="it.type==='iframe'?'0':'80px'"
            :class='{"form-item-no-label": !it.label}'
          >
            <FormItem  :item="it" :form="forms[item.name]" @formChange="formChange" />
          </el-form-item>
        </div>
        <slot :name="item.name"></slot>
      </el-form>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import FormItem from './formitem'
export default {
  name: "jvs-tab",
  components: {FormItem},
  props: {
    // 绑定值，选中选项卡的 name
    active: {
      type: String,
      default: ''
    },
    // 选项卡配置
    option: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 表单传递对象
    forms: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 表单结构对象
    formItem: {
      type: Object,
      default: () => {
        return {}
      }
    },
    formRef: {
      type: String,
      default: 'ruleForm'
    }
  },
  data () {
    return {
      activeName: '',
      defaultOption: {
        type: '', // tab风格， card/border-card
        tabPosition: 'top', // 选项卡所在位置, top/right/bottom/left
        stretch: false, // 标签的宽度是否自撑开
        column: [
          {
            label: '', // 选项卡标题
            disabled: false, // 是否禁用
            name: '', // 与选项卡绑定值 value 对应的标识符，表示选项卡别名, 该选项卡在选项卡列表中的顺序值，如第一个选项卡则为'1'
            lazy: false, // 标签是否延迟渲染
            permisionFlag: '', // 权限标识
          }
        ]
      }
    }
  },
  created () {
    this.dealPermission()
    if(this.active) {
      this.activeName = this.active
    }
  },
  methods: {
    handleClick (tab, event) {
      this.activeName = tab.name
      this.$emit('tab-click', tab.name)
    },
    // 处理权限
    dealPermission () {
      for(let i in this.option.column) {
        if(!this.option.column[i].permisionFlag) {
          this.option.column[i].show = true
        }else{
          this.option.column[i].show = this.$permissionMatch(this.option.column[i].permisionFlag)
        }
      }
      this.option.column = this.option.column.filter(item => item.show)
    },
    // 值变化
    formChange (form) {
      this.$set(this.forms, this.activeName, form)
      this.$forceUpdate()
      this.$emit('formChange', this.forms)
    }
  }
}
</script>
