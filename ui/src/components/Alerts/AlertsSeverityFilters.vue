<template>
  <div
    class="list"
    data-test="severity-list"
  >
    <AlertsSeverityCard
      v-for="severity in Object.keys(SeverityType).filter((a: string | number) => isNaN(Number(a)))"
      :key="severity"
      :severity="severity"
    />
  </div>
</template>

<script lang="ts" setup>
import { SeverityType } from '@/components/Alerts/alerts.constant'

const severityItems = Object.keys(SeverityType).length
const gap = 1.5
const itemGap = `${gap}%`
const listItemWidth = `${100 - (gap * (severityItems - 1)) / severityItems}%` // to set card with equal width
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.list {
  display: flex;
  flex-direction: row;
  gap: v-bind(itemGap);
  margin-bottom: var(variables.$spacing-l);
  > * {
    width: v-bind(listItemWidth);
  }
}
</style>
