<template>
  <div class="detection-method">
    <div class="title">Detection Method</div>
    <FeatherTabContainer class="tab-container">
      <template v-slot:tabs>
        <FeatherTab>Threshold Alert</FeatherTab>
        <FeatherTab>Event</FeatherTab>
      </template>
      <FeatherTabPanel class="panel"><ThresholdAlert /></FeatherTabPanel>
      <FeatherTabPanel class="panel">Event </FeatherTabPanel>
    </FeatherTabContainer>
  </div>
</template>

<script lang="ts" setup>
import { FeatherTab, FeatherTabContainer, FeatherTabPanel } from '@featherds/tabs'

import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'

const store = useMonitoringPoliciesStore()
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.detection-method {
  margin-top: var(variables.$spacing-xxl);
  .title {
    @include typography.headline4;
  }
}

:deep(.feather-input-sub-text) {
  display: none;
}
</style>
