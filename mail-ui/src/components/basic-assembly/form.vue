<template>
  <el-form
    :model="formDatas"
    :ref="refs || defalutSet.refs"
    :option="option || defalutSet.option"
    :class="{'jvs-form': true, 'jvs-form-autoflexable': (option.labelWidth == 'auto' || option.formAuto )}"
    :size="$store.state.params.form.size || option.size || 'mini'"
    :inline="option.inline || defalutSet.option.inline"
    :label-position="option.formAlign || defalutSet.option.formAlign"
    :label-width="option.labelWidth || defalutSet.option.labelWidth"
    :disabled='option.disabled'
  >
    <slot name="formTop"></slot>
    <el-row style="width:100%;">
      <!-- v-if="!option.isSearch || (option.isSearch && item.search == true)" -->
      <el-col
        v-for="item in option.column"
        :key="item.prop"
        :span="isSearch==true?(item.searchSpan || option.searchSpan || 24):(item.span || option.span || 24)"
        v-show="item.display == false ? item.display : true"
        :class="{'no-label-form-item': item.hideLabel}"
      >
        <el-form-item
          :class='{"form-item-no-label": ( (!item.label && item.type != "tab") || (["tableForm","divider","p","section"].indexOf(item.type) > -1) ), "form-item-no-label-tab": (!item.label && item.type == "tab")}'
          :label="item.label"
          :prop="item.prop"
          v-if="(item.type !='title' && (!item.children || item.children.length == 0)) && $permissionMatch(item.permisionFlag) && (item.display == false ? item.display : true)"
          :rules="item.rules"
        >
          <FormItem
            v-if="!item.formSlot"
            :form="formDatas"
            :item="item"
            @formChange="formChange"
            :roleOption="roleOption"
            :userList="userList"
            :departmentList="departmentList"
            :postList="postList"
          />
          <!-- 自定义列插槽 -->
          <div v-if="item.formSlot">
            <slot :name="item.prop+'Form'"></slot>
            <!-- 右侧提示 -->
            <el-tooltip
              v-if="item.tips && item.tips.position == 'right' && item.tips.text"
              class="form-item-tooltip"
              effect="dark"
              :content="item.tips.text"
              placement="top"
            >
              <i class="el-icon-question"></i>
            </el-tooltip>
          </div>
           <!-- 换行提示 -->
          <el-row
            v-if="item.tips && item.tips.position == 'bottom'"
            class="form-item-tips"
          >
            <span v-html="item.tips.text"></span>
          </el-row>
        </el-form-item>
        <h5 v-if="item.type == 'title'">{{item.label}}</h5>
        <!-- 子表单项 -->
        <el-row v-if="item.children && item.children.length > 0">
          <el-form-item
            :label="item.label"
            :prop="item.prop"
            :rules="item.rules"
            v-if="(item.display == false ? item.display : true)"
            :class='(!item.label || (["tableForm","divider","p","tab","section"].indexOf(item.type) > -1))? "form-item-no-label" : ""'
          >
            <FormItem
              v-if="!item.formSlot"
              :form="formDatas"
              :item="item"
              @formChange="formChange"
              :roleOption="roleOption"
              :userList="userList"
              :departmentList="departmentList"
              :postList="postList"
            />
            <!-- 自定义列插槽 -->
            <div v-if="item.formSlot">
              <slot :name="item.prop+'Form'"></slot>
              <!-- 右侧提示 -->
              <el-tooltip
                v-if="item.tips && item.tips.position == 'right' && item.tips.text"
                class="form-item-tooltip"
                effect="dark"
                :content="item.tips.text"
                placement="top"
              >
                <i class="el-icon-question"></i>
              </el-tooltip>
            </div>
            <!-- 换行提示 -->
            <el-row
              v-if="item.tips && item.tips.position == 'bottom'"
              class="form-item-tips"
            >
              <span v-html="item.tips.text"></span>
            </el-row>
          </el-form-item>
          <el-col
            v-for="it in item.children"
            :key="it.prop"
            :span="isSearch==true?(it.searchSpan || option.searchSpan || 24):(it.span || option.span || 24)"
            v-if="(formDatas[item.prop] == it.linkbind)"
          >
            <el-form-item
              :label="it.label"
              :prop="it.prop"
              v-if="$permissionMatch(it.permisionFlag) && (!option.isSearch || (option.isSearch && it.search == true)) && (it.display == false ? it.display : true)"
              :rules="it.rules"
              :class='(!it.label || (["tableForm","divider","p","tab","section"].indexOf(it.type) > -1))? "form-item-no-label" : ""'
            >
              <FormItem
                v-if="!it.formSlot"
                :form="formDatas"
                :item="it"
                @formChange="formChange"
                :roleOption="roleOption"
                :userList="userList"
                :departmentList="departmentList"
                :postList="postList"
              />
              <!-- 自定义列插槽 -->
              <div v-if="it.formSlot">
                <slot :name="it.prop+'Form'"></slot>
                <!-- 右侧提示 -->
                <el-tooltip
                  v-if="it.tips && it.tips.position == 'right' && it.tips.text"
                  class="form-item-tooltip"
                  effect="dark"
                  :content="it.tips.text"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </div>
              <!-- 换行提示 -->
              <el-row
                v-if="it.tips && it.tips.position == 'bottom'"
                class="form-item-tips"
              >
                <span v-html="it.tips.text"></span>
              </el-row>
            </el-form-item>
          </el-col>
        </el-row>
      </el-col>
      <el-col
        :span="option.isSearch ? 6 :  24"
        v-if="option.column.length > 0 && option.btnHide!==true"
        class="form-item-btn"
      >
        <el-form-item class="form-btn-bar">
          <template v-if="isSearch">
            <el-button
              size="mini"
              v-if="!(option.searchBtn == false)"
              type="primary"
              @click="submitForm(refs || defalutSet.refs)"
              :loading="option.submitLoading"
            >{{option.searchBtnText || defalutSet.option.searchBtnText}}</el-button>
            <el-button
              size="mini"
              v-if="!(option.emptyBtn == false)"
              @click="resetForm(refs || defalutSet.refs)"
            >{{option.emptyBtnText || defalutSet.option.emptyBtnText}}</el-button>
          </template>
          <template v-if="!isSearch">
            <el-button
              size="mini"
              v-if="!(option.submitBtn == false)"
              type="primary"
              @click="submitForm(refs || defalutSet.refs)"
              :loading="option.submitLoading"
            >{{option.submitBtnText || defalutSet.option.submitBtnText}}</el-button>
            <el-button
              size="mini"
              v-if="!(option.emptyBtn == false)"
              @click="resetForm(refs || defalutSet.refs)"
            >{{option.emptyBtnText || defalutSet.option.emptyBtnText}}</el-button>
            <el-button
              size="mini"
              v-if="!(option.cancal == false)"
              @click="$emit('cancalClick')"
            >{{option.cancalBtnText || defalutSet.option.cancalBtnText}}</el-button>
          </template>
          <slot name="formButton"></slot>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
import FormItem from './formitem'
import {getDeptList, getRoleList, getPostList} from '../api'
export default {
  name: "jvsForm",
  components: { FormItem },
  props: {
    // 是否为搜索表单
    isSearch: {
      type: Boolean,
      default: false
    },
    // 表单绑定
    refs: {
      type: String,
      default: 'ruleForm'
    },
    // 表单对象
    formData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 表单数据
    defalutFormData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 表单设置
    option: {
      type: Object,
      default: () => {
        return {
          formAlign: 'right', //对其方式
          inline: false, // 表单项是否可以同行,当垂直方向空间受限且表单较简单时，可以在一行内放置表单
          labelWidth: 'auto', // label宽
          searchBtn: true,
          searchBtnText: '',
          submitBtn: true, // 提交按钮是否显示，默认显示
          submitBtnText: '保存', // 提交按钮文字，默认 提交
          emptyBtn: true, // 重置按钮，默认显示
          emptyBtnText: '', // 重置按钮文字，默认 重置
          isSearch: false, // 是否为搜索表单
          cancal: true, // 取消按钮， 默认显示
          cancalBtnText: '取消',
          column: [ // 字段
            {
              label: '', // 文字
              prop: '', // 字段
              type: '', // 文本类型，默认input
              dicData: [], // 字典数据
              span: 24, // 表单项栅格比，默认24
              formSlot: false, // 是否自定义
              permisionFlag: '', // 权限标识
              rules: [], // 校验规则
              linkbind: '', // 父级联动控制对应的表单值
            }
          ],
        }
      }
    },
  },
  computed: {
    // formDatas: {
    //   get: function () {
    //     return this.formData
    //   },
    //   set: function () { }
    // }
  },
  data () {
    return {
      formDatas: {}, // 表单对象
      defalutSet: {
        refs: 'ruleForm',
        option: {
          formAlign: 'right', //对其方式
          inline: false, // 表单项是否可以同行,当垂直方向空间受限且表单较简单时，可以在一行内放置表单
          labelWidth: 'auto', // label宽
          searchBtn: true, // 搜索按钮是否显示，默认显示
          searchBtnText: '查询', // 搜索按钮，默认查询
          submitBtn: true, // 提交按钮是否显示，默认显示
          submitBtnText: '提交', // 提交按钮文字，默认 提交
          emptyBtn: true, // 重置按钮，默认显示
          emptyBtnText: '重置', // 重置按钮文字，默认 重置
          cancalBtnText: '取消', // 取消按钮文字， 默认 取消
        }
      },
      roleOption: [], // 角色列表
      userList: [], // 用户列表
      departmentList: [], // 部门列表
      postList: [], // 岗位列表
      clearAll: false, // 重置是否为初始对象{}
    }
  },
  created () {
    this.formDatas = this.formData
    if (this.defalutFormData) {
      for (let i in this.defalutFormData) {
        this.formDatas[i] = this.defalutFormData[i]
      }
    }
    if(JSON.stringify(this.formDatas) == '{}') {
      this.clearAll = true
    }
    this.getConst()
  },
  methods: {
    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$emit('submit', this.formDatas)
        } else {
          console.log('error submit!!')
          return false;
        }
      });
    },
    resetForm (formName) {
      if (this.option.isSearch === true) {
        this.formDatas = {}
      } else {
        if(this.clearAll) {
          this.formDatas = {}
        }else{
          this.$refs[formName].resetFields()
        }
      }
    },
    formChange (form) {
      this.$emit('formChange', form)
    },
    getConst () {
      let deptBool = false
      let roleBool = false
      let postBool = false
      // 优化：表单内无对应公共组件不发请求
      for(let i in  this.option.column) {
        if(this.option.column[i].type == 'department') {
          deptBool = true
        }
        if(this.option.column[i].type == 'role') {
          roleBool = true
        }
        if(this.option.column[i].type == 'post') {
          postBool = true
        }
        // 加入自定义校验
        if(this.option.column[i].regularExpression) {
          let str = '/' + this.option.column[i].regularExpression + '/'
          let _this = this
          this.option.column[i].rules.push({
            validator: function(rule, value, callback) {
              if(eval(str).test(value)) {
                callback()
              }else{
                let msg = '正则校验不通过'
                if(_this.option.column[i].regularMessage) {
                  msg = _this.option.column[i].regularMessage
                }
                callback(new Error(msg));
              }
            },
            trigger: ['blur', 'change']
          })
        }
      }
      if(deptBool) {
        getDeptList().then(res => {
          if(res.data.code == 0) {
            this.departmentList = res.data.data
          }
        })
      }
      if(roleBool) {
        getRoleList().then(res => {
          if(res.data.code == 0) {
            this.roleOption = res.data.data
          }
        })
      }
      if(postBool) {
        getPostList().then(res => {
          if(res.data.code == 0) {
            this.postList = res.data.data
          }
        })
      }
    }
  }
};
</script>

<style lang="scss">
.jvs-form {
  h5 {
    margin: 0;
    padding: 0;
    margin-bottom: 10px;
  }
  .el-form-item.form-btn-bar {
    // margin: 10px;
    .el-form-item__content {
      text-align: center;
      margin-left: 0 !important;
    }
  }
  .no-label-form-item{
    .el-form-item{
      .el-form-item__content{
        margin-left: 0!important;
      }
    }
  }
}
.jvs-form-autoflexable {
  .el-form-item {
    display: flex;
    margin: 0 10px;
    .el-form-item__label-wrap {
      margin-left: 0 !important;
      word-break: keep-all;
    }
    .el-form-item__content {
      margin-left: 0 !important;
      flex: 1;
      div {
        .el-input,
        .el-select {
          width: 100%;
        }
      }
    }
  }
}
.form-item-tooltip {
  display: inline-block;
  margin: 0 10px;
}
.form-item-no-label-tab{
  >.el-form-item__content{
    margin-left: 0!important;
  }
}
.form-item-no-label{
  .el-form-item__content{
    margin-left: 0!important;
  }
}
.form-item-tips{
  font-size: 12px;
  color: #c3c3c3;
  width: 100%;
}
</style>
