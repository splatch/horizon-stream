<template>
  <ul class="anchor-list">
    <li v-for="anchor in anchors" :key="anchor.label">
      <label>{{ anchor.label }}: </label>
      <a :href="anchor.link">{{ anchor.value }}</a>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'

interface Anchor {
  label: string,
  value: string | number,
  link: string
}

defineProps({
  anchors: {
    type: Array as PropType<Anchor[]>,
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