<template>
  <div class="list">
    <div class="card">
      Interface Util
      <div>
        <FeatherIcon
          class="icon"
          :icon="Icons.EditMode"
        />
        <FeatherIcon
          class="icon"
          :icon="Icons.ContentCopy"
        />
      </div>
    </div>
    <div class="card">Discards</div>
  </div>
</template>

<script lang="ts" setup>
import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'
import ContentCopy from '@featherds/icon/action/ContentCopy'
import EditMode from '@featherds/icon/action/EditMode'

import { markRaw } from 'vue'
const Icons = markRaw({
  ContentCopy,
  EditMode
})
const store = useMonitoringPoliciesStore()
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

    .icon {
      font-size: 20px;
      margin-left: 7px;
      cursor: pointer;
      color: var(variables.$primary);
    }
  }
}
:deep(.feather-input-sub-text) {
  display: none;
}
</style>
