<template>
  <label :for="item.type">{{ item.type }}</label>
  <FeatherChip :id="item.type" :class="`bg-status ${item.status}`">
    {{ item.type === 'status' ? item.status : getHumanReadableDuration(item.timestamp as number, item.timeUnit) }}
  </FeatherChip>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { FeatherChip } from '@featherds/chips'
import { Chip } from '@/types/metric'
import { getHumanReadableDuration } from '@/components/utils'

defineProps({
  item: {
    type: Object as PropType<Chip>,
    required: true
  }
})
</script>

<style lang="scss" scoped>
@use "@/styles/_statusBackground";

label {
  display: block;
  text-transform: capitalize;
}
</style>