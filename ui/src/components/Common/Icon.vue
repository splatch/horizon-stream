<script lang="ts" setup>
import { PropType } from 'vue'
import { setViewBox } from '@/components/utils'
import { IIcon } from '@/types'

const props = defineProps({
  icon: {
    type: Object as PropType<IIcon>,
    required: true
  }
})

const iconSize = props.icon.size || 'inherit'
</script>

<template>
  <FeatherTooltip
    :title="icon.tooltip || ''"
    v-slot="{ attrs, on }"
  >
    <FeatherIcon
      v-bind="icon.tooltip ? attrs : null" 
      v-on="on" :icon="icon.image" 
      :title="icon.title" 
      :viewBox="setViewBox(icon.image)" 
    />
  </FeatherTooltip>
</template>

<style lang="scss" scoped>
// FeatherIcon default width/height: 1rem
.btn-content > svg {
  width: v-bind(iconSize);
  height: v-bind(iconSize);
}
</style>
