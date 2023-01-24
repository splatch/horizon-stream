<template>
  <FeatherDropdown>
    <template v-slot:trigger="{ attrs, on }">
      <FeatherButton
        secondary
        class="btn-dropdown"
        :class="{ disabled: props.isDisabled }"
        v-bind="attrs"
        v-on="on"
        :disabled="props.isDisabled"
        :style="{ width: size + 'px' }"
      >
        <div class="btn-content">
          {{ selectedItem.name }}
          <FeatherIcon :icon="Icons.UnfoldMore" />
        </div>
      </FeatherButton>
    </template>
    <FeatherDropdownItem
      @click="setSelectedItem(item)"
      v-for="item of props.list"
      :key="item.id"
      >{{ item.name }}
    </FeatherDropdownItem>
  </FeatherDropdown>
</template>

<script lang="ts" setup>
import UnfoldMore from '@featherds/icon/navigation/UnfoldMore'
import { markRaw } from 'vue'
const emit = defineEmits(['dropdown-item-selected'])
const Icons = markRaw({
  UnfoldMore
})

const props = defineProps<{
  list: Record<string, string>[] // accept the structure [{id, name}]
  size?: number
  preselectedOption?: number //index number in the list parameter
  isDisabled?: boolean
}>()

const selectedItem = ref(props.list[props.preselectedOption] || props.list[0])
const setSelectedItem = (selected: Record<string, string>) => {
  selectedItem.value = selected
  emit('dropdown-item-selected', selected.id)
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';

.btn-dropdown {
  height: 40px;
  border: 1px solid var(variables.$secondary-text-on-surface) !important;
  .btn-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  &.disabled {
    border: 1px solid var(variables.$shade-4) !important;
  }
}
</style>
