<template>
  <FeatherSelect
    v-model="state"
    :options="selectType.options"
    :text-prop="selectType.optionText"
    :label="selectType.label"
    @update:modelValue="selectType.cb"
  />
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { ISelectDropdown } from '@/types/select'

defineProps({
  selectType: {
    type: Object as PropType<ISelectDropdown>,
    required: true
  }
})

const state = ref(undefined)
</script>

<style lang="scss" scoped>
</style>