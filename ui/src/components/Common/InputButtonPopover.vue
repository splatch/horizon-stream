<template>
  <FeatherPopover :pointer-alignment="alignment" :placement="placement">
    <template #default>
      <div class="input-add-popover">
        <FeatherInput
          label="Type..."
          v-model="inputValue"
        />
        <FeatherButton @click="handler(inputValue)" :disabled="!inputValue" primary>{{ label }}</FeatherButton>
      </div>
    </template>
    <template #trigger="{ attrs, on }">
      <FeatherButton v-bind="attrs" v-on="on" class="add-btn"
        >
        <Icon :icon="icon" />
      </FeatherButton
      >
    </template>
  </FeatherPopover>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { PointerAlignment, PopoverPlacement } from '@featherds/popover'
import Add from '@featherds/icon/action/Add'
import { IIcon } from '@/types'

defineProps({
  placement: {
    type: Object as PropType<PopoverPlacement>,
    default: PopoverPlacement.top
  },
  alignment: {
    type: Object as PropType<PointerAlignment>,
    default: PointerAlignment.center
  },
  icon: {
    type: Object as PropType<IIcon>,
    default: () => ({
      image: markRaw(Add),
      size: '2rem'
    })
  },
  label: {
    type: String,
    default: 'Add'
  },
  handler: {
    type: Function,
    required: true
  }
})

const inputValue = ref()
  
</script>

<style lang="scss" scoped>
@use "@featherds/styles/themes/variables";

:deep(.feather-popover-container) {
  > .popover {
    // to style popover
  }
}
.add-btn {
  color: white;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  min-width: inherit;
  padding: 1rem;
  line-height: inherit;
  background-color: var(variables.$shade-2);
  // TODO: how to remove the weird blue border when button clicked
  :deep {
    > .btn-content {
      display: block;
      > svg {
        left: -1rem; // TODO: how to set dynamically using addIcon.size value
        top: -1rem;
      }
    }
  }
}
</style>