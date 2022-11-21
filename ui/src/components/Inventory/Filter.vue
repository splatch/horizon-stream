<template>
  <!-- Search -->
  
  <!-- Node type -->
  <!-- <SelectDropdown :select-type="nodeType" data-test="select-node" /> -->
  <FeatherSelect
    v-model="nodeTypeState"
    :options="nodeType.options"
    :text-prop="nodeType.optionText"
    :label="nodeType.label"
    @update:modelValue="onNodeTypeSelect"
    data-test="select-node"
  />

  <!-- Monitoring Location -->

  <!-- Severity -->

  <!-- Tagging -->

  <!-- Sort: A-Z -->
</template>

<script lang="ts" setup>
import { fncArgVoid } from '@/types'
import { ISelectDropdown } from '@/types/select'

const emits = defineEmits(['selectedItem'])

// Node Type
const nodeTypeState = ref(undefined)
const onNodeTypeSelect: fncArgVoid = (selectedItem: any) => {
  emits('selectedItem', selectedItem)
}
const nodeType: ISelectDropdown = {
  label: 'Node Type',
  options: [
    {
      id: 1,
      type: 'type1'
    },
    {
      id: 2,
      type: 'type2'
    },
    {
      id: 3,
      type: 'type3'
    }
  ],
  optionText: 'type'
}
</script>

<style lang="scss" scoped></style>