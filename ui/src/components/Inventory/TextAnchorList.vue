<template>
  <ul class="text-anchor-list">
    <li data-test="profile">
      <label>{{ label.profile }}: </label>
      <a :href="anchor?.profileLink">{{ anchor?.profileValue }}</a>
    </li>
    <li data-test="location">
      <label>{{ label.location }}: </label>
      <a :href="anchor?.locationLink">{{ anchor?.locationValue }}</a>
    </li>
    <li data-test="ip-interface">
      <label>{{ label.ipInterface }}: </label>
      <a :href="anchor?.ipInterfaceLink">{{ anchor?.ipInterfaceValue }}</a>
    </li>
    <li data-test="tag">
      <label>{{ label.tag }}: </label>
      <a :href="anchor?.tagLink">{{ anchor?.tagValue }}</a>
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
  ipInterface: 'IP Interface',
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