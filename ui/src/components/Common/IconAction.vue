<template>
  <component :is="asLi ? 'li' : 'div'" :title="item?.title" >
    <FeatherIcon @click="item.action" :icon="item.icon" :title="item?.title" :viewBox="setViewBox(item.icon)" class="pointer" />
  </component>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { IconAction } from '@/types'
import { setViewBox } from '@/components/utils'



defineProps({
  item: {
    type: Object as PropType<IconAction>,
    required: true
  },
  asLi: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
@use "@featherds/styles/themes/variables";

.feather-icon {
  font-size: 1.5rem;
  color: var(variables.$secondary-text-on-surface); // TODO: get color from props
  &:hover {
    color: var(variables.$disabled-text-on-surface); // TODO: get color from props
  }
}
</style>