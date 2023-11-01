<template>
  <el-dropdown trigger="click" class="dropdownItem">
    <span class="el-dropdown-link">{{ prefix }}&nbsp;<span v-if="showActiveName">{{ activeName }}</span> <i class="el-icon-arrow-down el-icon--right" style="margin-left: 10px;"></i></span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item @click.native="handleClick(item)" v-for="(item) in option" :key="item.value">{{ item.label }}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "DropDown",
  props: {
    initActiveName: {
      type: String | Number,
      default: ''
    },
    // 是否显示 active
    showActiveName: {
      type: Boolean,
      default: true
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
  background: #F4F4F4;
  padding: 4px 20px;
  display: block;
  cursor: pointer;
}
</style>
