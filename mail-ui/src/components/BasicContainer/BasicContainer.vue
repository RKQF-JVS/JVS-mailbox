<template>
  <div class="basic-container"
       :class="{'basic-container--block':block, 'hideSideBar-container': !sidebar.opened}">
    <Card :useBorder="useBorder">
      <slot></slot>
    </Card>
  </div>
</template>

<script>
import Card from "@/components/Card/Card"
export default {
  name: "BasicContainer",
  props: {
    block: {
      type: Boolean,
      default: false
    },
    useBorder: {
      type: Boolean,
      default: true
    }
  },
  components: {
    Card
  },
  data() {
    return {
    }
  },
  computed: {
    sidebar() {
      return this.$store.state.app.sidebar
    }
  }
}
</script>

<style lang="scss">
.basic-container {
  margin-bottom: 36px;
  margin-top: 10px;
  padding: 8px 10px;
  border-radius: 10px;
  box-sizing: border-box;
  height: calc(100vh - 64px);
  width: calc(100vw - 220px);
  overflow-y: scroll;
  .el-card {
    width: 100%;
  }
  &:first-child {
    padding-top: 0;
  }
  &--block {
    height: 100%;
    .el-card {
      height: 100%;
    }
  }
}
.hideSideBar-container {
  width: calc(100vw - 260px)!important;
}
</style>
