<template>
  <div :class="['card', useBorder ? '' : 'card-no-border']">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: "Card",
  props: {
    useBorder: {
      type: Boolean,
      default: true
    }
  }
}
</script>

<style scoped>
.card {
  border: 1px solid #EBEEF5;
  border-bottom: none;
  border-top: none;
  background-color: #FFF;
  color: #303133;
  -webkit-transition: .3s;
  transition: .3s;
}
.card-no-border {
  border: none!important;
}
</style>
