<template>
  <component :is="tag" :title="propsData.label" class="container" data-test="propsData-chip">
    <label v-if="propsData.label" :for="propsData.label">{{ propsData.label }}</label>
    <FeatherChip :id="propsData.label" :class="`bg-status ${propsData.bgStatus}`" data-test="chip">
      {{ propsData.value }}
    </FeatherChip>
  </component>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { FeatherChip } from '@featherds/chips'
import { Chip } from '@/types/metric'
import { getHumanReadableDuration } from '@/components/utils'

const props = defineProps({
  metric: {
    type: Object as PropType<Chip>,
    required: true
  },
  tag: {
    type: String,
    default: 'div'
  }
})

const propsData = computed(() => {
  let chip = {
    label: props.metric.label,
    value: props.metric.status || '--',
    bgStatus: props.metric.status || 'unknown'
  }
  
  if('timestamp' in props.metric) {
    const bgStatus = 
      props.metric.timestamp === undefined 
        ? 'unknown' : props.metric?.timestamp && props.metric?.timestamp >= 0
          ? 'up' : 'down'

    chip = {
      ...chip,
      value: getHumanReadableDuration(props.metric?.timestamp as number),
      bgStatus
    }
  }

  return chip
})
</script>

<style lang="scss" scoped>
@use "@featherds/styles/themes/variables";
@use "@/styles/_statusBackground";

.container {
  text-align: center !important;
  box-shadow: none !important;
}

label {
  display: block;
  text-transform: capitalize;
  color: var(variables.$primary-text-on-surface);
}

.chip {
  margin: 0;
}
</style>