import Vue from 'vue'
import bulletin from '@/components/bulletin/index.vue'
const bulletinBox = Vue.extend(bulletin)
bulletin.install = function (data) {
  let instance = new bulletinBox({data})
  instance.$store = this.$root.$store
  instance.$mount()

  document.body.appendChild(instance.$el)

  Vue.nextTick(() => {
    instance.getBulletinList()
  })
}

export default bulletin
