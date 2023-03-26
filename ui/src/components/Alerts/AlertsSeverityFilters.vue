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
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.list {
  display: flex;
  flex-direction: row;
  gap: var(variables.$spacing-m);
  margin-bottom: var(variables.$spacing-l);
  > * {
    flex-grow: 1;
  }
}
</style>
