<template>
  <div class="datasource-info-list">
    <div>
      <el-input size="mini" placeholder="请选择数据" :disabled="disableBool" v-model="userStr" class="input-with-select" @focus="enableinputHandle">
        <div slot="append">
          <jvs-button icon="el-icon-search" type="info" :disabled="disableBool" @click="openDialog"></jvs-button>
          <jvs-button icon="el-icon-delete" type="warning" :disabled="disableBool" @click="clearUser"></jvs-button>
        </div>
      </el-input>
    </div>
    <el-dialog
      title="选择数据"
      v-if="dialogVisible"
      :visible.sync="dialogVisible"
      :before-close="handleClose"
      append-to-body
      width="90%"
      class="datasource-info-list-dialog"
    >
      <div class="datasource-info-box">
        <jvs-table
          class="table-info-table"
          :loading="tableLoading"
          :page="page"
          :option="option"
          :data="tableData"
          :selectable="selectable"
          @on-load="getList"
          @row-click="selectOne"
          @search-change="searchChange"
          @selection-change="selectChange"
        >
          <template slot="roleName" slot-scope="scope">
            <span>{{scope.row.roleName | formatRoleName}}</span>
          </template>
        </jvs-table>
      </div>
      <el-row style="margin-top:10px;display:flex;justify-content:center;align-items:center;">
        <jvs-button size="mini" type="primary" @click="submit">确定</jvs-button>
        <jvs-button size="mini" @click="cancel">取消</jvs-button>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import { getDataSourceInfoList } from "../api";
import {columnTypeList} from '@/const/const'

// 表单项
import MInput from '@/plugin/assembly/input'
import MTextarea from '@/plugin/assembly/textarea'
import MInputNumber from '@/plugin/assembly/inputNumber'
import MSwitch from '@/plugin/assembly/switch'
import MTimepicker from '@/plugin/assembly/timepicker'
import MDatePicker from '@/plugin/assembly/datePicker'
import MSelect from '@/plugin/assembly/select'
export default {
  name: "datasource-info-list",
  components: {},
  props: {
    item: {
      type: Object
    },
    form: {
      type: Object
    },
    prop: {
      type: String
    },
    selectable: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    disable: {
      type: Boolean
    }
  },
  computed: {},
  filters: {
    formatRoleName (list) {
      let str = ''
      if(list) {
        str = list.join(',')
      }
      return str
    }
  },
  data () {
    return {
      queryParams: {},
      tableLoading: false,
      tableData: [],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 20, // 每页显示多少条
        pageSizes: [20, 50, 100, 200, 500, 1000]
      },
      option: {
        page: true,
        align: 'center',
        menuAlign: 'center',
        viewBtn: false,
        addBtn: false,
        menu: false,
        selection: true,
        search: true,
        showOverflow: true,
        isSearch: true,
        labelWidth: 'auto',
        searchBtnText: '查询',
        highlightCurrentRow: true,
        cancal: false,
        column: []
      },
      selected: {},
      dialogVisible: false,
      userStr: '',
      userList: [],
      userNameList: [],
      disableBool: false
    }
  },
  methods: {
    init () {
      if(this.item && this.item.tableName) {
        if(this.item.infoColumnFields && this.item.infoColumnFields.length > 0) {
          let temp = this.eachColumnList(this.item.infoColumnFields)
          this.option.column = temp
        }
      }
    },
    // 遍历字段列表生成表单项
    eachColumnList (list) {
      let temp = []
      for(let i in list) {
        let columnTemp = this.getIteTypeOfForm(list[i].dataType)
        if(!columnTemp) {
          columnTemp = {
            type: 'select'
          }
        }
        let obj = {}
        switch(columnTemp.type) {
          case 'input':
            obj = new MInput();
            obj.placeholder = '请输入' + list[i].columnComment;
            break;
          case 'textarea':
            obj = new MTextarea();
            obj.placeholder = '请输入' + list[i].columnComment;
            break;
          case 'inputNumber':
            obj = new MInputNumber();
            obj.placeholder = '请输入' + list[i].columnComment;
            break;
          case 'switch':
            obj = new MSwitch();
            break;
          case 'timePicker':
            obj = new MTimepicker();
            obj.placeholder = '请选择' + list[i].columnComment;
            break;
          case 'datePicker':
            obj = new MDatePicker();
            obj.placeholder = '请选择' + list[i].columnComment;
            break;
          case 'select':
            obj = new MSelect();
            obj.multiple = false;
            if(list[i].seniorSetting && list[i].seniorSetting.enumValues) {
              obj.dicData = list[i].seniorSetting.enumValues
            };
            obj.sqlType = 'enum';
            obj.placeholder = '请选择' + list[i].columnComment;
            break;
          default :
            obj = new MInput();
            obj.placeholder = '请输入' + list[i].columnComment;
            break;
        }
        obj.label = list[i].columnComment
        obj.prop = list[i].fieldName
        obj.searchSpan = 6
        obj.search = true
        if(columnTemp.datetype) {
          obj.datetype = columnTemp.datetype
        }
        if(columnTemp.num == 'int') {
          obj.precision = 0
        }
        temp.push(obj)
      }
      return temp
    },
    // 根据数据类型获取表单项组件类型
    getIteTypeOfForm (type) {
      for(let i in columnTypeList) {
        if(columnTypeList[i].value == type) {
          return columnTypeList[i]
        }
      }
    },

    getList (isFirst) {
      let bool = true
      let list = ['databaseName', 'tableName', 'infoColumn', 'showProp', 'sendProp']
      for(let i in list) {
        if(this.item[list[i]]) {
          bool &= true
          if(list[i] == 'infoColumn' && this.item.infoColumn.length == 0) {
            bool &= false
          }
        }else{
          bool &= false
        }
      }
      if(bool) {
        let obj={
          databaseName: this.item.databaseName,
          tableName: this.item.tableName,
          selectedViewFieldName: this.item.infoColumn,
          displayValueFieldName: this.item.showProp,
          passValueFieldName: this.item.sendProp
        }
        let queryConditions = {}
        queryConditions = Object.assign(queryConditions, this.queryParams)
        let query = {
          current: this.page.currentPage,
          size: this.page.pageSize
        }
        if(queryConditions && JSON.stringify(queryConditions) != '{}') {
          query.queryConditions = queryConditions
        }
        this.tableLoading = true
        getDataSourceInfoList(Object.assign(obj, query)).then(res => {
          if (res.data.code==0) {
            this.tableData=res.data.data.records
            this.page.total=res.data.data.total
            this.page.currentPage=res.data.data.current
            this.tableLoading = false
            if(isFirst) {
              if(this.form[this.prop]) {
                if(this.selectable) {
                  let temp = []
                  for(let i in this.tableData) {
                    if(this.form[this.prop].indexOf(this.tableData[i][this.item.sendProp]) > -1) {
                      temp.push(this.tableData[i][this.item.showProp]) // userName
                    }
                  }
                  this.userStr = temp.join(',')
                  this.userNameList = temp
                  this.userList = this.form[this.prop]
                }else{
                  this.userStr = this.form[this.item.showProp]
                  for(let i in this.tableData) {
                    if(this.form[this.prop] == this.tableData[i][this.item.sendProp]) {
                      this.userStr = this.tableData[i][this.item.showProp] // userName
                    }
                  }
                }
              }
            }
          }
        }).catch(e => {
          this.tableLoading = false
        })
      }
    },
    selectOne (data) {
      this.selected = data.row
    },
    searchChange (form) {
      this.queryParams=form
      this.getList()
    },
    selectChange (data) {
      let temp = []
      let nm = []
      for(let i in data) {
        temp.push(data[i][this.item.sendProp])
        nm.push(data[i][this.item.showProp])
      }
      this.userList = temp
      this.userNameList = nm
    },
    submit () {
      if(this.selectable) {
        this.userStr = this.userNameList.join(',')
        this.form[this.prop] = this.userList
      }else{
        if(this.selected) {
          this.form[this.prop] = this.selected[this.item.sendProp]
          this.userStr = this.selected[this.item.showProp]
        }
      }
      this.handleClose()
    },
    cancel () {
      this.$emit('cancel', true)
      this.handleClose()
    },
    openDialog () {
      this.dialogVisible = true
    },
    handleClose () {
      this.dialogVisible = false
    },
    enableinputHandle () {
      this.openDialog()
    },
    clearUser () {
      this.userStr = ""
      if(this.selectable) {
        this.form[this.prop] = []
      }else{
        if(this.selected) {
          this.form[this.prop] = ""
        }
      }
    }
  },
  mounted () {},
  created () {
    this.disableBool = this.disable
    this.init()
    this.getList(true)
  }
};
</script>

<style lang="scss">
.datasource-info-box{
  display: flex;
  width: 100%;
  overflow: hidden;
  .user-dept-tree{
    width: 150px;
  }
  .table-info-table{
    flex: 1;
    margin-left: 20px;
    width: calc(100% - 200px);
    box-sizing: border-box;
    .el-card{
      box-shadow: none;
    }
    .el-card__body{
      border: 0;
    }
    .el-table__body-wrapper{
      height: auto!important;
    }
  }
}
</style>
