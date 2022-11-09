<template>
  <ul class="anchor-list">
    <li>
      <label>{{ anchor.profileLabel }}: </label>
      <a :href="anchor.profileLink">{{ anchor.profileValue }}</a>
    </li>
    <li>
      <label>{{ anchor.locationLabel }}: </label>
      <a :href="anchor.locationLink">{{ anchor.locationValue }}</a>
    </li>
    <li>
      <label>{{ anchor.ipInterfaceLabel }}: </label>
      <a :href="anchor.ipInterfaceLink">{{ anchor.ipInterfaceValue }}</a>
    </li>
    <li>
      <label>{{ anchor.tagLabel }}: </label>
      <a :href="anchor.tagLink">{{ anchor.tagValue }}</a>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'

interface Anchor {
  profileLabel: string,
  profileValue: number,
  profileLink: string,
  locationLabel: string,
  locationValue: string,
  locationLink: string,
  ipInterfaceLabel: string,
  ipInterfaceValue: number,
  ipInterfaceLink: string,
  tagLabel: string,
  tagValue: number,
  tagLink: string,
}

defineProps({
  anchor: {
    type: Object as PropType<Anchor>,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.anchor-list {
  list-style: none;
  padding: 0;
}
</style>