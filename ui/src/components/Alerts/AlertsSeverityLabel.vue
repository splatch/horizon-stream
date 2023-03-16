<template>
  <span
    v-if="severity"
    class="severity-status"
    :class="[`${severity.toLowerCase()}-color`]"
    >{{ severity }}</span
  >
</template>

<script lang="ts" setup>
defineProps<{
  severity?: string
}>()
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@/styles/_severities';

.severity-status {
  padding: 2px 8px;
  font-size: 11px;
  border-radius: 5px;
  font-weight: 600;
  width: fit-content;
  max-height: 27px;
  @include typography.caption();
}
</style>
