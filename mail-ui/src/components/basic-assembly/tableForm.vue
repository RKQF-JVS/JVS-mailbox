<template>
  <div class="table-form">
    <jvs-table :option="options" :data="tableData">
      <template slot="menu" slot-scope="scope">
        <slot name="menuBtn" :row="scope.row" :index="scope.index"></slot>
      </template>
      <template v-for="(item, index) in options.tableColumn" :slot="item.prop" slot-scope="scope">
        <div :key="item.prop+'node'+index">
          <div v-if="item.text && item.text.label && scope.row[item.prop] == item.text.value" :key="item.prop+'text'">
            <span>{{item.text.label}}</span>
          </div>
          <div v-if="item.needSlot !== true && !(item.text && item.text.label && scope.row[item.prop] == item.text.value) && displayByBind(item, scope.row)" :key="item.prop+'item'">
            <tableFormItem
              :form="scope.row"
              :item="item"
              @formChange="formChange"
            ></tableFormItem>
          </div>
          <div v-if="displayByBind(item, scope.row) && !(item.text && item.text.label && scope.row[item.prop] == item.text.value) && item.needSlot === true" :key="item.prop+'needslotitem'">
            <slot :name="item.prop+'Item'" :row="scope.row" :index="scope.index"></slot>
          </div>
        </div>
      </template>
    </jvs-table>
  </div>
</template>
<script>
import {getSelectData} from '@/api/index'
export default {
  name: 'table-Form',
  components: {
    // 异步import，formitem引用了tableForm，嵌套时异步引用
    tableFormItem: () => import('@/components/basic-assembly/formitem')
  },
  props: {
    option: {
      type: Object
    },
    data: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  computed: {
    options(){
      let temp = JSON.parse(JSON.stringify(this.option))
      if(temp.column && !temp.tableColumn) {
        temp.tableColumn = temp.column
      }
      for(let i in temp.tableColumn) {
        temp.tableColumn[i].slot = true
      }
      if(!temp.column) {
        temp.column = temp.tableColumn
      }
      return temp
    },
    tableData: {
      get () {
        return this.data
      },
      set () {}
    }
  },
  created () {
    this.init()
  },
  data () {
    return {}
  },
  methods: {
    init () {
      for(let i in this.options.tableColumn) {
        if(this.options.tableColumn[i].dicUrl) {
          getSelectData(this.options.tableColumn[i].dicUrl).then(res => {
            if(res.data.code == 0) {
               for(let sitem in res.data.data){
                if(typeof sitem == 'string') {
                  this.options.tableColumn[i].dicData.push({
                    label: res.data.data[sitem],
                    value: res.data.data[sitem]
                  })
                }else{
                  this.options.tableColumn[i].dicData.push({
                    label: res.data.data[sitem][this.options.tableColumn[i].props.label ? this.options.tableColumn[i].props.label : 'label'],
                    value: res.data.data[sitem][this.options.tableColumn[i].props.value ? this.options.tableColumn[i].props.value : 'value']
                  })
                }
              }
            }
          })
        }
        if(this.options.tableColumn[i].dicData) {
          let temp = []
          let bool = false
          for(let j in this.options.tableColumn[i].dicData) {
            if(typeof this.options.tableColumn[i].dicData[j] == 'string') {
              bool = true
              temp.push({
                label: this.options.tableColumn[i].dicData[j],
                value: this.options.tableColumn[i].dicData[j]
              })
            }
          }
          if(bool) {
            this.options.tableColumn[i].dicData = temp
          }
        }
      }
    },
    // 下拉选择change
    valueChange (item, row) {
      this.$emit((item.prop+'Change'), {
        item: item,
        row: row
      })
    },
    // 根据绑定字段值决定显隐
    displayByBind (item, row) {
      let temp = true
      if(item.display) {
        if(typeof item.display.value == 'object' && (item.display.value instanceof Array)) {
          if(item.display.value.indexOf(row[item.display.key]) > -1) {
            temp = true
          }else{
            temp = false
          }
        }else{
          if(row[item.display.key] == item.display.value) {
            temp = true
          }else{
            temp = false
          }
        }
      }else{
        temp = true
      }
      return temp
    },
    formChange (form) {
      this.$emit('formChange', form)
    },
  }
}
</script>
<style lang="scss">
.table-form{
  .el-card{
    border-width: 0;
  }
  .jvs-table{
    .el-table{
      .el-table__header-wrapper{
        margin-top: 0;
      }
    }
  }
  .table-body-box{
    .el-table__body-wrapper{
      height: auto!important;
    }
  }
  .el-table__row{
    .el-input{
      font-size: 12px;
      .el-input__inner{
        height: 28px;
        line-height: 28px;
      }
    }
  }
}
</style>
