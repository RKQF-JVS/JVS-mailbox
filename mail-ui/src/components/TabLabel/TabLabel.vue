<template>
  <div class="tabRow">
    <span class="tip"></span>
    <span @click="handleClick(item)"
          :class="activeName === item.name ? 'label-item label-active' : 'label-item label'"
          v-for="(item, index) in option"
          :key="item.name">{{ item.label }}</span>
  </div>
</template>

<script>
export default {
  name: "TabLabel",
  props: {
    initActiveName: {
      type: String,
      default: ''
    },
    option: {
      type: Array,
      required: true
    }
  },
  methods: {
    handleClick(item) {
      this.$emit('tabChange', item)
    }
  },
  computed: {
    activeName() {
      return this.initActiveName || this.option[0].name
    }
  }
}
</script>

<style lang="scss" scoped>
.tabRow {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 8px;
  width: 100%;
}
.tip {
  width: 4px;
  height: 18px;
  background: #3471FF;
}
.label-item {
  cursor: pointer;
  margin: 0 10px;
}
.label {
  font-size: 14px;
}
.label-active {
  font-size: 16px;
  color: #3471FF;
  font-weight: 600;
}
</style>
