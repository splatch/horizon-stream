<template>
  <div class="list">
    <div
      class="card"
      :class="{ selected: selectedAlert === 'InterfaceUtil' }"
      @click="setThresholdAlert('InterfaceUtil')"
    >
      Interface Util
    </div>
    <div
      class="card"
      :class="{ selected: selectedAlert === 'Discards' }"
      @click="setThresholdAlert('Discards')"
    >
      Discards
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'
const store = useMonitoringPoliciesStore()
const selectedAlert = ref('InterfaceUtil')

const setThresholdAlert = (type: string) => {
  selectedAlert.value = type
  store.setMetricName(type)
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.list {
  display: flex;
  margin-top: var(variables.$spacing-l);
  > .card {
    display: flex;
    justify-content: space-between;
    padding: var(variables.$spacing-l);
    border: 1px solid var(variables.$shade-4);
    background-color: var(variables.$shade-4);
    margin-right: var(variables.$spacing-m);
    width: 240px;
    border-radius: 5px;
    cursor: pointer;
    &.selected {
      border: 2px solid var(variables.$primary);
      @include typography.subtitle2;
    }
  }
}
:deep(.feather-input-sub-text) {
  display: none;
}
</style>
