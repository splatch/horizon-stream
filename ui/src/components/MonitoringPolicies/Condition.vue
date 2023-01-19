<template>
  <div class="condition">
    Trigger when the metric is
    <BasicDropdown
      :list="limitList"
      :size="130"
      @dropdown-item-selected="setTriggerLimit"
    />
    <Slider
      v-model="currentTime"
      :min="0"
      :max="100"
      class="slider-red"
      :tooltipPosition="'top'"
      :format="(num) => num + '%'"
      @change="timeChanged"
    />
    for any
    <BasicDropdown
      :list="timeList"
      :size="150"
      @dropdown-item-selected="setTriggerLimit"
    />
    during the last
    <BasicDropdown
      :list="lastPeriodList"
      :size="160"
      @dropdown-item-selected="setTriggerLimit"
    />
    as
    <BasicDropdown
      :list="severityList"
      :size="130"
      @dropdown-item-selected="setTriggerLimit"
    />
  </div>
</template>

<script lang="ts" setup>
import Slider from '@vueform/slider'
import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'

const props = defineProps<{
  id: number
}>()

const store = useMonitoringPoliciesStore()
const currentTime = ref(50)
const limitList = [
  { id: 'above', name: 'Above' },
  { id: 'equalTo', name: 'Equal to' },
  { id: 'below', name: 'Below' },
  { id: 'isNotEqual', name: 'Is Not Equal' }
]

//time durations
const keys = Array.from({ length: 12 }, (v, i) => 5 + i * 5)
const timeList = keys.map((key) => {
  return { id: key, name: key + ' minutes' }
})

//last event time
const keysSteps = Array.from({ length: 10 }, (v, i) => 15 + i * 15)
const lastPeriodList = keysSteps.map((key) => {
  return { id: key, name: key + ' minutes' }
})

//severities
const severityList = [
  { id: 'critical', name: 'Critical' },
  { id: 'major', name: 'Major' },
  { id: 'minor', name: 'Minor' },
  { id: 'warning', name: 'Warning' }
]
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.condition {
  display: flex;
  align-items: center;
  padding: var(variables.$spacing-l);
  border: 1px solid var(variables.$shade-4);
  border-radius: 10px;
  margin-top: var(variables.$spacing-xs);

  > * {
    margin: 0 var(variables.$spacing-s);
  }
}

.slider {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  align-items: center;
}
:root {
  .slider-horizontal {
    width: 200px;
    --slider-tooltip-distance: 10px;
    --slider-tooltip-bg: #273180;
    --slider-connect-bg: #273180;
  }
}

:deep(.feather-input-sub-text) {
  display: none;
}
</style>
<style src="@vueform/slider/themes/default.css"></style>
