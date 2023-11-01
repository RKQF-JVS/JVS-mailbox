<template>
  <el-dropdown trigger="click" class="dropdownItem">
    <span class="el-dropdown-link">{{ prefix }}{{ activeName }}<i class="el-icon-arrow-down el-icon--right" style="margin-left: 10px;"></i></span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item
        @click.native="handleClick(item)"
        :divided="item.divided || false"
        v-for="(item) in option"
        :key="item.value">{{ item.label }}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "DropDownSelect",
  props: {
    initActiveName: {
      type: String | Number,
      default: ''
    },
    option: {
      type: Array,
      required: true
    },
    prefix: {
      type: String,
      default: '按'
    }
  },
  methods: {
    handleClick(item) {
      this.$emit('selectChange', item)
    }
  },
  computed: {
    activeName() {
      const item = this.option.filter(item => item.value === this.initActiveName)
      return item[0] ? item[0].label : ''
    }
  }
}
</script>

<style lang="scss" scoped>

.dropdownItem {
  display: block;
  cursor: pointer;
  font-size: 12px;
}
.el-dropdown-menu__item {
  line-height: 22px;
  padding: 0 14px;
  font-size: 12px;
  color: #606266;
}
.el-dropdown-menu__item--divided {
  margin-top: 4px;
  border-top: 1px dashed #EBEEF5;
}
.el-dropdown-menu__item--divided:before {
  margin: 0 -14px;
}
.el-dropdown-link {
  width: 80px;
  height: 24px;
  display: block;
  line-height: 24px;
  text-align: center;
  font-size: 13px;
  color: #454545;
}
</style>
