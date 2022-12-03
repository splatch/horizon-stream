<template>
  <ul class="text-anchor-list">
    <li data-test="profile">
      <label :for="label.profile">{{ label.profile }}: </label>
      <a v-if="anchorProps.profileLink" @click="router.push({path: anchorProps.profileLink})" :id="label.profile">{{ anchor?.profileValue }}</a>
      <!-- <a v-if="anchorProps.profileLink" :href="anchorProps.profileLink" :id="label.profile">{{ anchor?.profileValue }}</a> -->
      <!-- <a v-if="anchor.profileLink" :href="anchorProps.profileLink" :id="label.profile">{{ anchor?.profileValue }}</a> -->
      <span v-else :id="label.profile">{{ anchor?.profileValue }}</span>
    </li>
    <li data-test="location">
      <label :for="label.location">{{ label.location }}: </label>
      <a v-if="anchor?.locationLink" :href="anchor?.locationLink" :id="label.location">{{ anchor?.locationValue }}</a>
      <span v-else :id="label.location">{{ anchor?.locationValue }}</span>
    </li>
    <li data-test="management-ip">
      <label :for="label.managementIp">{{ label.managementIp }}: </label>
      <a v-if="anchor?.managementIpLink" :href="anchor?.managementIpLink" :id="label.managementIp">{{ anchor?.managementIpValue }}</a>
      <span v-else :id="(label.managementIp)">{{ anchor?.managementIpValue }}</span>
    </li>
    <li data-test="tag">
      <label :for="label.tag">{{ label.tag }}: </label>
      <a v-if="anchor?.tagLink" :href="anchor?.tagLink" :id="label.tag">{{ anchor?.tagValue }}</a>
      <span v-else :id="label.tag">{{ anchor?.tagValue }}</span>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { Anchor } from '@/types/inventory'
// import { Router } from 'vue-router'
import router from '@/router'

const props = defineProps<{
  anchor: Anchor
}>()
/* const props = defineProps({
  anchor: {
    type: Object as PropType<Anchor>,
    required: true
  }
}) */

const anchorProps = computed(() => ({
  ...props,
  // profileLink: props.profileLink
  profileLink: '/'
}))

const xss = () => {
  console.log('XSS!!!')
  // alert('XSS!!!')
  // router.replace({ path: '/' })
  router.push({ path: '/' })
}
console.log(router)

const label = {
  profile: 'Monitoring Profile',
  location: 'Monitoring Location',
  managementIp: 'Management IP',
  tag: 'Tag'
}
</script>

<style lang="scss" scoped>
ul.text-anchor-list {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}
</style>