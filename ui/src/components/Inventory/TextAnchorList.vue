<template>
  <ul class="text-anchor-list">
    <li data-test="profile">
      <label :for="label.profile">{{ label.profile }}: </label>
      <a v-if="anchor?.profileLink" :href="anchor?.profileLink" :id="label.profile">{{ anchor?.profileValue }}</a>
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

defineProps({
  anchor: {
    type: Object as PropType<Anchor>,
    required: true
  }
})

/**
 * TODO: with real data
 * - monitoringLocationId query
 */
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