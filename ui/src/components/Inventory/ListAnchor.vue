<template>
  <FeatherList>
    <FeatherListItem v-for="anchor in anchors" :key="anchor.label" :asLi="true">{{ anchor.label }}: <a :href="anchor.link">{{ anchor.value }}</a></FeatherListItem>
  </FeatherList>
</template>

<script lang="ts" setup>
defineProps({
  anchors: {
    type: Object,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.feather-list-item {
  cursor: default;
  height: auto;
  &:hover {
    background: none !important;
  }
}
</style>