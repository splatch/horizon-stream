<template>
  <div class="list">
    <div
      v-for="severity in Object.keys(AlertType).filter((a: string | number) => isNaN(Number(a)))"
      :key="severity"
    >
      <AlertsSeverityCard
        :severity="severity"
        :count="severitiesGrouped[severity] ? severitiesGrouped[severity].length : 0"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { AlertType } from '@/components/Alerts/alerts.constant'
import { useAlertsStore } from '@/store/Views/alertsStore'
import { groupBy } from 'lodash'
const store = useAlertsStore()
const severitiesGrouped = computed(() => groupBy(store.allAlertsList, 'severity'))
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
