<template>
  {{ item.header }}
</template>

<script lang="ts" setup>
defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>

<style lang="scss" scoped></style>