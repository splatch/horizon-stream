<template>
  <FeatherSelect
    class="severity-select"
    v-model="selectedSeverity"
    :options="options"
    text-prop="option"
    @update:modelValue="onSeveritySelect"
    label="Show Severity >="
  />
</template>

<script
  setup
  lang="ts"
>
import { useMapStore } from '@/store/Views/mapStore'
import { FeatherSelect } from '@featherds/select'

const mapStore = useMapStore()

const options = [
  { id: 'NORMAL', option: 'Normal' },
  { id: 'WARNING', option: 'Warning' },
  { id: 'MINOR', option: 'Minor' },
  { id: 'MAJOR', option: 'Major' },
  { id: 'CRITICAL', option: 'Critical' }
]
const selectedSeverity = ref(options[0])

const onSeveritySelect = () => mapStore.selectedSeverity = selectedSeverity.value.id

// positioning the severity element in the header bar
const elemWidth = 250
const rightContainerWidth = document.querySelector('.right.center-horiz > .right-container')?.clientWidth || 0
const elemPosRight = `${rightContainerWidth}px`
</script>

<style lang="scss">
@use "@featherds/styles/themes/variables";

.severity-select {
  position: absolute;
  width: v-bind(elemWidth);
  right: v-bind(elemPosRight);
  margin-right: 5rem;
  top: 11px;
  z-index: var(variables.$zindex-tooltip);
  .feather-input-wrapper {
    background: var(variables.$primary-text-on-color);
    border: 2px solid var(variables.$secondary);
  }
  .feather-input-label {
    border: 2px solid var(variables.$secondary);
    border-bottom: none;
  }
}
</style>

