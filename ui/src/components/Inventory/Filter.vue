<template>
  <!-- Search -->
  
  <!-- Node type -->
  <SelectDropdown :select-type="selectNode" data-test="select-node" />

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
const onSelect: fncArgVoid = (selectedItem) => {
  emits('selectedItem', selectedItem)
}
const selectNode: ISelectDropdown = {
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
  optionText: 'type',
  cb: onSelect
}
</script>

<style lang="scss" scoped></style>