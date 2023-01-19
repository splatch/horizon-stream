<template>
  <FeatherDropdown>
    <template v-slot:trigger="{ attrs, on }">
      <FeatherButton
        secondary
        class="btn-dropdown"
        v-bind="attrs"
        v-on="on"
      >
        <div
          class="btn-content"
          :style="{ width: size + 'px' }"
        >
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
  list: Record<string, string>[]
  size?: number
}>()
const selectedItem = ref(props.list[0])
const setSelectedItem = (selected: Record<string, string>) => {
  selectedItem.value = selected
  emit('dropdown-item-selected', selected.id)
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';

.btn-dropdown {
  height: 40px;
  border: 1px solid var(variables.$shade-2) !important;
  .btn-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: v-bind(size) + 'px';
  }
}
</style>
