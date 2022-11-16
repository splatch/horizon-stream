<template>
  <div class="container" data-test="metric-chip">
    <label :for="item.type">{{ item.type }}</label>
    <FeatherChip :id="item.type" :class="`bg-status ${itemBgColor}`" data-test="chip">
      {{ itemText }}
    </FeatherChip>
  </div>
  
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { FeatherChip } from '@featherds/chips'
import { Chip } from '@/types/metric'
import { getHumanReadableDuration } from '@/components/utils'

const props = defineProps({
  item: {
    type: Object as PropType<Chip>,
    required: true
  }
})

const itemBgColor = props.item.status.toLowerCase()

const itemText = props.item.type === 'status' ? props.item.status : getHumanReadableDuration(props.item.timestamp as number, props.item.timeUnit)
</script>

<style lang="scss" scoped>
@use "@/styles/_statusBackground";

.container {
  text-align: left;
}

label {
  display: block;
  text-transform: capitalize;
}
</style>