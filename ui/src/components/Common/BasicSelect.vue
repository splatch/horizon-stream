<template>
  <FeatherSelect
    :style="{ width: size + 'px' }"
    label=""
    :options="list"
    text-prop="name"
    v-model="selectedItem"
    hideLabel
    :disabled="isDisabled"
    @update:modelValue="setSelectedItem"
  >
  </FeatherSelect>
</template>

<script lang="ts" setup>
const emit = defineEmits(['item-selected'])

const props = defineProps<{
  list: Record<string, string>[] // accept the structure [{id, name}]
  size?: number
  isDisabled?: boolean
}>()

const selectedItem = ref(props.preselectedOption ? props.list[props.preselectedOption] : props.list[0])
const setSelectedItem = (selected: Record<string, string>) => {
  emit('item-selected', selected.id)
}
</script>

<style scoped lang="scss">
:deep(.label-border) {
  width: 0 !important;
}
</style>
