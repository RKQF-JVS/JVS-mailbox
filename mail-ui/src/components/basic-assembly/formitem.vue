<template>
  <div class="jvs-form-item" style="display:flex;align-items:center;">
    <el-input
      v-model="forms[item.prop]"
      v-if='item.type==="input" || !item.type'
      :show-word-limit="item.showwordlimit"
      :minlength="item.minlength"
      :maxlength="item.maxlength"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :show-password="item.showpassword"
      :disabled="item.disabled"
      :prefix-icon="item.prefixicon"
      :suffix-icon="item.suffixicon"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @blur="formChange"
    >
      <template v-if='item.prepend' slot="prepend">{{item.prepend}}</template>
      <template v-if='item.append' slot="append">{{item.append}}</template>
    </el-input>
    <span class="el-form-item__error" v-if='errorShow'>{{item.regularMessage}}</span>
    <el-input
      v-model="forms[item.prop]"
      v-if='item.type==="InputReadOnly" || item.type==="inputReadOnly"'
      :disabled="item.disabled || true"
      :placeholder="item.placeholder || item.label"
      :size="$store.state.params.form.size || item.size || 'mini'"
    ></el-input>
    <el-input
      type="textarea"
      v-if='item.type==="textarea"'
      v-model="forms[item.prop]"
      :rows="item.rows"
      :show-word-limit="item.showwordlimit"
      :minlength="item.minlength"
      :maxlength="item.maxlength"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :disabled="item.disabled"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @blur="formChange"
    ></el-input>
    <el-input
      type="textarea"
      v-if='item.type==="textareaReadOnly"'
      v-model="forms[item.prop]"
      rows="2"
      :placeholder="item.placeholder || item.label"
      :disabled="item.disabled || true"
      :size="$store.state.params.form.size || item.size || 'mini'"
    ></el-input>
    <el-input-number
      v-if='item.type==="inputNumber"'
      v-model="forms[item.prop]"
      :min="item.min"
      :max="item.max"
      :step="item.step"
      :step-strictly="item.stepstrictly"
      :precision="item.precision"
      :disabled="item.disabled"
      :controls-position="item.controlsposition"
      :placeholder="item.placeholder || item.label"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-input-number>

    <el-select
      v-if='item.type==="select"'
      v-model="forms[item.prop]"
      :placeholder="item.placeholder || item.label"
      :multiple="item.multiple"
      :collapse-tags="!item.collapsetags"
      :disabled="item.disabled"
      :clearable="item.clearable"
      :filterable="item.filterable"
      :allow-create="item.allowcreate"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="changeHandle"
    >
      <el-option
        v-for="(sitem) in selectOption"
        :key="sitem[(item.props && item.props.value) || 'value']+item.prop +Math.random() + Date.now().toString()"
        :label="sitem[(item.props && item.props.label) || 'label']"
        :value="sitem[(item.props && item.props.value) || 'value']"
      >
        <span style="float: left">{{ sitem[(item.props && item.props.label) || 'label'] }}</span>
        <span v-if="sitem.tip" style="float: right; color: #8492a6; font-size: 13px">{{ sitem.tip }}</span>
      </el-option>
    </el-select>

    <el-switch
      v-if='item.type==="switch"'
      v-model="forms[item.prop]"
      :disabled="item.disabled"
      :active-text="item.activetext"
      :inactive-text="item.inactivetext"
      :active-color="item.activecolor"
      :inactive-color="item.inactivecolor"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-switch>

    <el-slider
      v-if='item.type==="slider"'
      v-model="forms[item.prop]"
      :min="item.min"
      :max="item.max"
      :disabled="item.disabled"
      :step="item.step"
      :show-stops="item.showstops"
      :show-input="item.showinput"
      :range="item.range"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-slider>

    <el-time-select
      v-if='item.type==="timeSelect"'
      v-model="forms[item.prop]"
      :disabled="item.disabled"
      :clearable="item.clearable"
      :picker-options="item.pickeroptions"
      :placeholder="item.placeholder || item.label"
      :prefix-icon="item.prefixicon"
      value-format="HH:mm:ss"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-time-select>

    <el-time-picker
      v-if='item.type==="timePicker"'
      v-model="forms[item.prop]"
      :disabled="item.disabled"
      :clearable="item.clearable"
      :placeholder="item.placeholder || item.label"
      :prefix-icon="item.prefixicon"
      :is-range="item.isrange"
      :start-placeholder="item.startplaceholder"
      :end-placeholder="item.endplaceholder"
      :range-separator="item.rangeseparator"
      value-format="HH:mm:ss"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-time-picker>

    <el-date-picker
      v-if='(item.type==="datePicker") && ( item.datetype=="date" || item.datetype=="dates" || item.datetype=="daterange")'
      v-model="forms[item.prop]"
      :type="item.datetype"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :disabled="item.disabled"
      :prefix-icon="item.prefixicon"
      value-format="yyyy-MM-dd"
      :start-placeholder="item.startplaceholder"
      :end-placeholder="item.endplaceholder"
      :range-separator="item.rangeseparator"
      :picker-options="startEndLimitHandle"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-date-picker>
    <el-date-picker
      v-if='(item.type==="datePicker") && item.datetype=="week"'
      v-model="forms[item.prop]"
      type="week"
      format="yyyy 第 WW 周"
      value-format="yyyy-MM-dd"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :disabled="item.disabled"
      :prefix-icon="item.prefixicon"
      :start-placeholder="item.startplaceholder"
      :end-placeholder="item.endplaceholder"
      :range-separator="item.rangeseparator"
      :picker-options="startEndLimitHandle"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-date-picker>
    <el-date-picker
      v-if='(item.type==="datePicker")&& ( item.datetype=="month"|| item.datetype=="monthrange" )'
      v-model="forms[item.prop]"
      :type="item.datetype"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :disabled="item.disabled"
      :prefix-icon="item.prefixicon"
      value-format="yyyy-MM"
      :start-placeholder="item.startplaceholder"
      :end-placeholder="item.endplaceholder"
      :range-separator="item.rangeseparator"
      :picker-options="startEndLimitHandle"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-date-picker>
    <el-date-picker
      v-if='(item.type==="datePicker") && item.datetype=="year"'
      v-model="forms[item.prop]"
      type="year"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :disabled="item.disabled"
      :prefix-icon="item.prefixicon"
      value-format="yyyy"
      :start-placeholder="item.startplaceholder"
      :end-placeholder="item.endplaceholder"
      :range-separator="item.rangeseparator"
      :picker-options="startEndLimitHandle"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-date-picker>
    <el-date-picker
      v-if='(item.type==="datePicker") && (item.datetype=="datetime" || item.datetype=="datetimerange")'
      v-model="forms[item.prop]"
      :type="item.datetype"
      :placeholder="item.placeholder || item.label"
      :clearable="item.clearable"
      :disabled="item.disabled"
      :prefix-icon="item.prefixicon"
      value-format="yyyy-MM-dd HH:mm:ss"
      :start-placeholder="item.startplaceholder"
      :end-placeholder="item.endplaceholder"
      :range-separator="item.rangeseparator"
      :picker-options="startEndLimitHandle"
      :default-value="item.defaultValue"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    ></el-date-picker>

    <el-radio-group
      v-if='(item.type==="radio")'
      v-model="forms[item.prop]"
      :disabled="item.disabled"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="changeHandle"
    >
      <div v-if='item.radiotype==="yuan" || !item.radiotype'>
        <el-radio
          v-for="(item2) in item.dicData"
          :key="item2[(item.props && item.props.value) || 'value']+item2[(item.props && item.props.label) || 'label']+'yuan'"
          :label="item2[(item.props && item.props.value) || 'value']"
        >{{item2[(item.props && item.props.label) || 'label']}}</el-radio>
      </div>
      <div v-if='item.radiotype==="button"'>
        <el-radio-button
          v-for="(item2) in item.dicData"
          :key="item2[(item.props && item.props.value) || 'value'] + item2[(item.props && item.props.label) || 'label'] +'but'"
          :label="item2[(item.props && item.props.value) || 'value']"
        >{{item2[(item.props && item.props.label) || 'label']}}</el-radio-button>
      </div>
    </el-radio-group>

    <el-checkbox-group
      v-if='(item.type==="checkbox")'
      v-model="forms[item.prop]"
      :disabled="item.disabled"
      :border="item.border"
      :min="item.min"
      :max="item.max"
      :size="$store.state.params.form.size || item.size || 'mini'"
      @change="formChange"
    >
      <div v-if='(item.checkboxtype=== "fang" || !item.checkboxtype) && item.dicData'>
        <el-checkbox
          v-for="(item2) in item.dicData"
          :key="item2[(item.props && item.props.value) || 'value']+item.label"
          :label="item2[(item.props && item.props.value) || 'value']"
        >{{item2[(item.props && item.props.label) || 'label']}}</el-checkbox>
      </div>
      <div v-if='item.checkboxtype=== "button" && item.dicData'>
        <el-checkbox-button
          v-for="(item2) in item.dicData"
          :key="'checkbut'+item2[(item.props && item.props.value) || 'value']+item.label"
          :label="item2[(item.props && item.props.value) || 'value']"
        >{{item2[(item.props && item.props.label) || 'label']}}</el-checkbox-button>
      </div>
    </el-checkbox-group>

    <!-- 颜色选择器 -->
    <el-color-picker v-if="(item.type == 'colorSelect')" v-model="forms[item.prop]" :placeholder="item.placeholder || item.label" @change="formChange"></el-color-picker>

    <!-- 图片 -->
    <ul
      class="el-upload-list el-upload-list--picture-card"
      v-if='item.type==="image"'
    >
      <li
        tabindex="0"
        class="el-upload-list__item is-success"
        v-for="mi in forms[item.prop]"
        :key="'image'+mi.url"
        @click="handlePictureCardPreview(mi.url)"
      >
        <el-image style="width: 100%; height: 100%;" :src="mi.url" :fit="item.fit || 'contain'">
          <div slot="error" class="image-slot loading-back" style="position:absolute;">
            <i class="el-icon-loading" style="font-size: 24px;color:#999;"></i>
          </div>
        </el-image>
      </li>
      <li
        tabindex="0"
        class="el-upload-list__item"
        v-if='!forms[item.prop] || forms[item.prop].length==0'
      >
        <el-image style="width: 100%; height: 100%;" src :fit="item.fit || 'contain'">
          <div slot="error" class="image-slot loading-back" style="position:absolute;">
            <i class="el-icon-loading" style="font-size: 24px;color:#999;"></i>
          </div>
        </el-image>
      </li>
    </ul>

    <!-- 上传图片 -->
    <el-upload
      v-if='(item.type === "imageUpload")'
      :class="item.fileList.length < (item.limit ? item.limit : 5) ? 'form-list-upload-img' : 'form-list-upload-img-none'"
      :action="item.action"
      :multiple="item.multipleUpload"
      :limit="item.limit || 5"
      :headers="item.headers || {}"
      :file-list="item.fileList || []"
      list-type="picture-card"
      :on-success="item.handleSuccess"
      :on-error="item.handleError"
      :on-preview="handlePictureCardPreviewUpload"
      :on-remove="item.handleRemove"
      :on-change="uploadChangeHandle"
    >
      <i class="el-icon-plus"></i>
    </el-upload>

    <!-- 预览图片 -->
    <el-dialog
      v-if='item.type === "imageUpload" || item.type === "image"'
      class="preViewDialog"
      :visible.sync="dialogVisible"
    >
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>

    <!-- 上传文件 -->
    <el-upload
      v-if='item.type === "fileUpload"'
      :class="item.fileList.length < (item.limit ? item.limit : 5) ? 'form-list-upload-file' : 'form-list-upload-file-none'"
      :action="item.action"
      :multiple="item.multipleUpload"
      :limit="item.limit"
      :headers="item.headers"
      :file-list="item.fileList"
      :size="$store.state.params.form.size || item.size || 'mini'"
      :on-success="item.handleSuccess"
      :on-error="item.handleError"
      :on-remove="item.handleRemove"
      :on-change="uploadChangeHandle"
    >
      <el-button slot="trigger" size="mini" type="primary">选取文件</el-button>
    </el-upload>

    <!-- 文件列表 -->
    <ul class="el-upload-list el-upload-list--text" v-if="item.type==='file'">
      <li
        class="el-upload-list__item is-success"
        v-for="fi in forms[item.prop]"
        :key="'file'+fi.url"
      >
        <a
          class="el-upload-list__item-name"
          target="_blank"
          :href="fi.url?fi.url:'javascript:void(0)'"
        >
          <i class="el-icon-document"></i>
          {{fi.name}}
        </a>
        <label class="el-upload-list__item-status-label">
          <i class="el-icon-upload-success el-icon-circle-check"></i>
        </label>
        <i class="el-icon-close"></i>
      </li>
      <li class="el-upload-list__item" v-if='!forms[item.prop] || forms[item.prop].length==0'>
        <a class="el-upload-list__item-name">
          <i class="el-icon-document"></i>
          {{'文件名称'}}
        </a>
      </li>
    </ul>

    <!-- 选项卡 -->
    <jvs-tab :formRef="formRef" v-if="item.type=='tab'" :active="item.activeName" :formItem="item" :forms="forms[item.prop]" :option="{type:'card', column: item.dicData}" @tab-click="tabClick" @formChange="tabFormchange">
    </jvs-tab>
    <!-- <el-tabs v-model="item.activeName" type="card" @tab-click="item.handleClick(item.activeName)" v-if="item.type==='tab'">
      <el-tab-pane v-for="ti in item.dicData" :key="'tab'+ti.value" :label="ti.label" :name="ti.value"></el-tab-pane>
    </el-tabs> -->

    <!-- 展示的表格 -->
    <!-- <el-table
      v-if="item.type==='TableReadOnly' || item.type==='tableReadOnly'"
      :data="forms[item.prop]"
      :border="item.border"
      highlight-current-row
      class="tb-edit"
      align="center"
      style="width: 100%;cursor:pointer;"
    >
      <el-table-column
        v-for="(ti) in item.option"
        :key="''+ti.value+'table'"
        :prop="ti.value"
        :label="ti.label"
        style="text-align:center;"
      ></el-table-column>
    </el-table> -->

    <!-- 描述框 -->
    <div
      v-if="item.type==='box'"
      :style="'line-height:24px;text-align:'+item.contentposition+';font-size:'+item.fontsize+'px;color:'+item.textcolor+';font-weight:'+item.fontweight+';'"
    >
      {{forms[item.prop]}}
      <span v-if='!forms[item.prop]'>{{item.text}}</span>
    </div>
    <!-- 链接 -->
    <a
      v-if="item.type =='link'"
      :href="forms[item.prop]?forms[item.prop]:'javascript:void(0);'"
      :target="item.openType"
      :style="'line-height:24px;text-align:'+item.contentposition+';font-size:'+item.fontsize+'px;color:'+item.textcolor+';font-weight:'+item.fontweight+';text-decoration:'+item.textdecoration+';'"
    >{{item.text}}</a>
    <!-- 嵌入页面 -->
    <div v-if="item.type==='iframe'" :style="'width:100%;height:'+item.iframeheight+';background:#ecf5ff;'">
      <iframe
        :name="item.id"
        :id="item.prop"
        :src="item.iframeurl"
        frameborder="0"
        width="100%"
        height="100%"
        scrolling="scroll"
      ></iframe>
    </div>

    <!-- 图标选择器 -->
     <div class="form-item-icon-selct" style="position: relative;" v-if="item.type == 'iconSelect'" :id="'icon-select-item-'+item.prop">
       <el-popover
          placement="bottom"
          :width="getWidth(item)"
          trigger="click">
          <div class="icon-select-tool">
            <i v-for="(it, itx) in iconList" :key="itx+it" :class="it" @click="checkIcon(item.prop, it)"></i>
          </div>
          <div slot="reference" style="display:flex;align-items: center;">
            <el-input v-model="forms[item.prop]" placeholder="图标"></el-input>
            <i v-if="forms[item.prop]" :class="forms[item.prop]" style="margin-left:10px;"></i>
          </div>
        </el-popover>
      </div>

    <!-- p文字 -->
    <p v-if="item.type === 'p'"  :style="{'text-align': item.contentposition,'font-size':item.fontsize+'px',color:item.textcolor}">{{item.text}}</p>

    <!-- 分割线 -->
    <el-divider v-if="item.type === 'divider'" :content-position='item.contentposition'>{{item.text}}</el-divider>

    <!-- 用户组件 -->
    <userForm v-if="item.type==='user'" :form="forms" :prop="item.prop" :selectable="item.multiple" :defaultValue="item.defaultValue" :enableinput="item.allowinput" />
    <!-- 部门组件 -->
    <el-cascader
      v-if="item.type==='department'"
      v-model="forms[item.prop]"
      size="mini"
      :options="departmentList"
      clearable
      :show-all-levels="item.showalllevels"
      :collapse-tags="!item.collapsetags"
      :props="{
        expandTrigger: 'hover',
        multiple: item.multiple === false ? item.multiple : true,
        children: 'childList',
        label: 'name',
        value: 'id',
        emitPath: item.emitPath
      }"
    >
    </el-cascader>
    <!-- 角色组件 -->
    <el-select v-model="forms[item.prop]" filterable placeholder="请选择角色" v-if="item.type==='role'" :multiple='item.multiple' @change="changeHandle">
      <el-option
        v-for="ir in roleOption"
        :key="ir.id+'role'+ir.roleName"
        :label="ir.roleName"
        :value="ir.id">
      </el-option>
    </el-select>
    <!-- 岗位组件 -->
    <el-select v-model="forms[item.prop]" filterable placeholder="请选择岗位" v-if="item.type==='post'"
      :multiple='item.multiple' @change="changeHandle">
      <el-option
        v-for="ir in postList"
        :key="ir.id+'post'+ir.name"
        :label="ir.name"
        :value="ir.id">
      </el-option>
    </el-select>

    <!-- 可编辑表格 -->
    <div v-if="item.type == 'tableForm'" style="flex:1;width:100%;">
      <tableForm :option="tableFormOption" :data="forms[item.prop]">
        <template slot="menuBtn" slot-scope="scope">
          <jvs-button type="text" @click="deleteRow(scope.row, scope.index)">删除</jvs-button>
        </template>
      </tableForm>
      <el-row style="margin-top:10px;">
        <jvs-button size="mini" @click="addRowHandle">新增</jvs-button>
      </el-row>
    </div>

    <!-- 计数器   滑块   显示单位 -->
    <span v-if="['inputNumber', 'slider'].indexOf(item.type) > -1 && item.unit" style="padding-left: 5px;">{{item.unit}}</span>

    <!-- 地区选择 -->
    <el-cascader
      v-if="item.type==='chinaArea'"
      v-model="forms[item.prop]"
      size="mini"
      :options="chinaAreaList"
      clearable
      :show-all-levels="item.showalllevels"
      :collapse-tags="!item.collapsetags"
      :props="{
        expandTrigger: 'hover',
        multiple: item.multiple === false ? item.multiple : true,
        label: 'name',
        value: item.emitKey ? item.emitKey : 'code',
        emitPath: item.emitPath
      }"
    >
    </el-cascader>

    <!-- 富文本 -->
    <div v-if="item.type === 'htmlEditor'" :id="item.prop+'-editor'"></div>

    <!-- 按钮 -->
    <jvs-button v-if="item.type === 'button'" :disabled="item.disabled" :type="item.buttonType" :round="item.buttonRound" :size="item.size" @click="btnClick">{{item.text}}</jvs-button>

    <!-- 级联选择 -->
    <el-cascader
      v-if="item.type==='cascader'"
      v-model="forms[item.prop]"
      size="mini"
      :options="cascaderList"
      clearable
      :show-all-levels="item.showalllevels"
      :collapse-tags="!item.collapsetags"
      :props="{
        expandTrigger: 'hover',
        multiple: item.multiple === false ? item.multiple : true,
        label: 'name',
        value: item.emitKey ? item.emitKey : 'value',
        emitPath: item.emitPath
      }"
    >
    </el-cascader>

    <!-- 数据源 -->
    <datasourceForm style="flex:1;" v-if="item.type==='datasource'" :item="item" :form="forms" :prop="item.prop" :selectable="item.multiple" />

    <!-- 右侧提示 -->
    <el-tooltip v-if="item.tips && item.tips.position == 'right' && item.tips.text" class="form-item-tooltip" effect="dark" :content="item.tips.text" placement="top">
      <i class="el-icon-question"></i>
    </el-tooltip>
  </div>
</template>

<script>
import userForm from './userForm'
import iconList from '@/const/iconfont'
import tableForm from '@/components/basic-assembly/tableForm'
import datasourceForm from './datasourceForm'
import systemIcon from '@/const/systemIcon'
// import {getSelectData, getFetchTable, getLineDataOfGet, getFormReviewData} from '../api/tableDesignsenior'
import {getSelectData} from '@/api/index'
import {areaList} from '@/const/chinaArea.js'
import {getClassifyDictTree, getSystemDictItems} from '@/api/newDesign'
import E from 'wangeditor'
export default {
  name: "formitem",
  components: {
    userForm,
    tableForm,
    datasourceForm
  },
  props: {
    // 表单对象
    form: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 表单内的组件对象
    item: {
      type: Object,
      default: () => {
        return {}
      }
    },
    formRef: {
      type: String,
      default: 'ruleForm'
    },
    // 用户列表
    userList : {
      type: Array,
      default: () => {
        return []
      }
    },
    // 角色列表
    roleOption: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 部门列表
    departmentList: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 岗位列表
    postList: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 是否需要刷新组件
    freshBoolean: {
      type: Boolean
    }
  },
  computed: {
    forms () {
      return this.form
    },
    tableFormOption: {
      get () {
        let obj = {
          addBtn: false,
          viewBtn: false,
          delBtn: false,
          editBtn: false,
          page: false,
          border: this.item.border,
          align: 'center',
          menuAlign: 'center',
          cancal: false,
          showOverflow: true,
          tableColumn: this.item.tableColumn
        }
        return obj
      },
      set () {}
    }
  },
  data () {
    return {
      selectOption: [],
      startEndLimitHandle: {
        disabledDate: time => {
          let bool=false
          if (!this.item.startLimit) {
            let end=new Date(this.item.endLimit).getTime()
            if (time.getTime()<=end) {
              bool=false
            } else {
              bool=true
            }
          }
          if (!this.item.endLimit) {
            let start=new Date(this.item.startLimit).getTime()
            if (time.getTime()>=start-8.64e7) {
              bool=false
            } else {
              bool=true
            }
          }
          if (!this.item.startLimit&&!this.item.endLimit) {
            bool=false
          }
          if (this.item.startLimit&&this.item.endLimit) {
            let start=new Date(this.item.startLimit).getTime()
            let end=new Date(this.item.endLimit).getTime()
            if (time.getTime()>=start-8.64e7&&time.getTime()<=end) {
              bool=false
            } else {
              bool=true
            }
          }
          return bool
        }
      },
      errorShow: false, // 自定义验证提示错误
      dialogVisible: false, // 预览图片弹框
      dialogImageUrl: '', // 预览图片地址
      iconList: [...iconList, ...systemIcon], // 图标列表
      chinaAreaList: areaList,
      cascaderList: [], // 级联选择数据
      editor: null, // 富文本
      contentValidate: false,
      pathArr: [], // 路径结果
    };
  },
  methods: {
    changeHandle (val) {
      this.$set(this.item, 'currVal', val)
      this.$set(this.forms, this.item.prop, val)
      this.formChange()
      this.$forceUpdate()
    },
    uploadChangeHandle (file, fileList) {
      // console.log('upload change.....')
      // console.log(fileList)
      this.$forceUpdate()
      let obj={}
      obj[this.item.prop]=fileList
      this.$emit('currentValueHandle', obj)
    },
    async initItem () {
      if (!this.item.dicUrl && !this.item.url) {
        if (this.item.dicData) {
          this.selectOption=this.item.dicData
        }
      } else {
        let url=this.item.dicUrl || this.item.url
        if (!url) {
          return false
        }
        getSelectData(url).then(res => {
          if(res.data.code === 0) {
            this.selectOption = []
            for(let sitem in res.data.data){
              if(typeof res.data.data[sitem] == 'string') {
                this.selectOption.push({
                  label: res.data.data[sitem],
                  value: res.data.data[sitem]
                })
              }else{
                // this.selectOption.push({
                //   label: res.data.data[sitem][this.item.props.label ? this.item.props.label : 'label'],
                //   value: res.data.data[sitem][this.item.props.value ? this.item.props.value : 'value']
                // })
                this.selectOption.push(res.data.data[sitem])
              }
            }
          }
        })
      }
      // 系统字典
      if(this.item.type == 'select' && this.item.systemDict) {
        this.selectOption = []
        await getSystemDictItems(this.item.systemDict).then(res => {
          if(res.data.code == 0 && res.data.data) {
            this.selectOption = res.data.data
          }
        })
      }
      if(this.item.type == 'checkbox') {
        !this.forms[this.item.prop] && (this.$set(this.forms, this.item.prop, []))
      }
      // 下拉切换是否多选时，初始化数据类型
      if(this.item.type == 'select') {
        if(this.item.multiple) {
          !this.forms[this.item.prop] && (this.$set(this.forms, this.item.prop, []))
        }else{
          if(!this.forms[this.item.prop] || this.forms[this.item.prop] instanceof Array) {
            this.$set(this.forms, this.item.prop, "")
          }
        }
      }
      // 滑块
      if(this.item.type == 'slider') {
        if(this.item.range) {
          !this.forms[this.item.prop] && (this.$set(this.forms, this.item.prop, [0, this.item.max / 2]))
        }else{
          if(!this.forms[this.item.prop] || this.forms[this.item.prop] instanceof Array) {
            this.$set(this.forms, this.item.prop, 0)
          }
        }
      }
      // 表单项默认值填充，权重小于表单初始化值
      if(this.item.defaultValue || this.item.defaultValue === false || this.item.defaultValue === "") {
        (!this.forms[this.item.prop] && this.forms[this.item.prop] !== false && this.forms[this.item.prop] !== 0) && (this.$set(this.forms, this.item.prop, this.item.defaultValue))
      }
      // 级联选择类
      if(this.item.type == 'cascader' && this.item.dictName) {
        getClassifyDictTree(this.item.dictName).then(res => {
          if(res.data.code == 0 && res.data.data && res.data.data.children) {
            this.cascaderList = res.data.data.children
          }
        })
      }
      // tab选项卡
      if(['tab'].indexOf(this.item.type) > -1) {
        if(this.item.dicData && this.item.dicData.length > 0) {
          !this.forms[this.item.prop] && (this.$set(this.forms, this.item.prop, {}))
          for(let col in this.item.column) {
            if(this.item.column[col] && this.item.column[col].length > 0) {
              !this.forms[this.item.prop][col] && (this.$set(this.forms[this.item.prop], col, {}))
            }
          }
        }
      }
    },
    handlePictureCardPreview (url) {
      this.dialogImageUrl=url
      this.dialogVisible=true
    },
    handlePictureCardPreviewUpload (file) {
      this.dialogImageUrl=file.url
      this.dialogVisible=true
    },
    checkIcon (key, icon) {
      this.form[key] = icon
      this.$forceUpdate()
    },
    // 字段值改变传出表单
    formChange () {
      this.$emit('formChange', this.form)
    },
    // 选项卡formchange
    tabFormchange (data) {
      this.$set(this.form, this.item.prop, data)
      this.$forceUpdate()
    },
    addRowHandle () {
      this.forms[this.item.prop].push({})
    },
    deleteRow (row, index) {
      this.forms[this.item.prop].splice(index, 1)
    },
    // 获取宽度占比
    getWidth (item) {
      let w = 400
      if(item.prop && document.getElementById('icon-select-item-'+item.prop)) {
        w = document.getElementById('icon-select-item-'+item.prop).clientWidth
      }
      return w
    },
    // 初始化富文本
    initEditor (prop) {
      let _this = this
      if(this.editor) {
        this.editor.destroy()
      }
      _this.editor = new E('#' + prop + '-editor')
      _this.editor.config.height = 400
      _this.editor.config.uploadImgShowBase64 = true
      _this.editor.config.menus = [
        'head',
        'bold',
        'fontSize',
        'fontName',
        'italic',
        'underline',
        'strikeThrough',
        'indent',
        'lineHeight',
        'foreColor',
        'backColor',
        'link',
        'list',
        'justify',
        'quote',
        'emoticon',
        'image',
        'table',
        'code',
        'splitLine',
        'undo',
        'redo',
      ]
      _this.editor.config.onblur = function (newHtml) {
        _this.form[prop] = newHtml
        if(newHtml) {
          _this.contentValidate = false
        }else{
          _this.contentValidate = true
        }
      }
      _this.editor.config.onchange = function (newHtml) {
        _this.form[prop] = newHtml
        if(newHtml) {
          _this.contentValidate = false
        }else{
          _this.contentValidate = true
        }
      }
      _this.editor.create()
      if(_this.form[prop]) {
        _this.editor.txt.html(_this.form[prop])
      }
      this.$forceUpdate()
    },
    tabClick (name) {
      if(this.item.handleClick) {
        this.item.handleClick(name)
      }
    },
    // 按钮点击
    btnClick () {
      if(this.item.openUrl) {
        this.$openUrl(this.item.openUrl, this.item.newWindowOpen ? '_blank' : '_self')
      }
    }
  },
  watch: {
    item: {
      handler () {
        this.initItem()
        // 下拉框 单选 自定义选择
        if(this.item.currVal) {
          if(this.item.multiple) {
            if(this.item.currVal instanceof Array !== true) {
              this.$set(this.forms, this.item.prop, [])
            }else{
              this.$set(this.forms, this.item.prop, this.item.currVal)
            }
          }else{
            if(this.item.currVal instanceof Array !== true) {
              this.$set(this.forms, this.item.prop, this.item.currVal)
            }else{
              this.$set(this.forms, this.item.prop, "")
            }
          }
        }
        if(this.item.type == 'htmlEditor') {
          this.initEditor(this.item.prop)
        }
      },
      deep: true
    },
    freshBoolean: {
      handler(newVal, oldVal) {
        if(this.item.type == 'htmlEditor') {
          this.initEditor(this.item.prop)
        }
      }
    }
  },
  created () {
    this.initItem()
  }
};
</script>

<style lang="scss">
.loading-back {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
}
.form-list-upload-img {
  .el-upload--picture-card {
    display: inline-block;
  }
}
.form-list-upload-img-none {
  .el-upload--picture-card {
    display: none;
  }
}
.form-list-upload-file {
  .el-upload--text {
    display: inline-block;
  }
}
.form-list-upload-file-none {
  .el-upload--text {
    display: none;
  }
}
.icon-select-tool{
  // margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  height: 200px;
  scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
  // box-shadow: 0 0 10px #ccc;
  i{
    margin: 10px;
    display: block;
    width: 20px;
    height: 20px;
    line-height: 20px;
    cursor: pointer;
  }
  i:hover{
    color: #409EFF;
  }
}
.icon-select-tool::-webkit-scrollbar {
  display: none; /* Chrome Safari */
}
.icon-select-tool-position{
  position: absolute;
  height: 158px;
  top: 45px;
  margin: 0;
  z-index: 9;
}
.jvs-form-item{
  min-height: 32px;
  .el-slider, p, .el-input-number, .el-select, .el-date-editor, .form-item-icon-selct, .el-tabs, .el-cascader, .user-info-list{
    flex: 1;
  }
}
</style>
