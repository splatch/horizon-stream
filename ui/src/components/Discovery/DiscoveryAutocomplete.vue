<!-- 
  Autocomplete search (tag, location,...) upon text entering in the input box. Search on input focus is disabled to prevent having a too long list.
  - new-value: list of values to be saved
  - get-items: method to call to get the list of values; endpoint query.
  - items: results from the endpoint query
  - render-type: single (default) | multi.
    - multi: does not allow to add new value if not exists in the list
  - allow-new: to add new value, if not exists in the list (applicable only to 'single' render-type)
  TODO:
    - prevent input box displays `undefined` after unselect value
 -->
<template>
  <pre>{{ modelValue }}</pre>
  <FeatherAutocomplete
    :model-value="modelValue"
    @update:model-value="updateModelValue"
    @search="search"
    @new="addValue"
    :loading="loading"
    :results="results"
    :label="(props.label as string)"
    :type="props.renderType"
    :allow-new="allowNew"
    class="tag-autocomplete"
  />
  <FeatherChipList
    v-if="props.renderType === 'single'"
    label="Item chip list with icon"
  >
    <FeatherChip
      v-for="item in modelValue"
      :key="item._text"
    >
      <span>{{ item._text }}</span>
      <template v-slot:icon
        ><FeatherIcon
          @click="unselectItem(item._text)"
          :icon="CancelIcon"
      /></template>
    </FeatherChip>
  </FeatherChipList>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import { IAutocompleteItemType } from '@featherds/autocomplete'
import CancelIcon from '@featherds/icon/navigation/Cancel'

type IAutomcomplete = IAutocompleteItemType & { _text: string }
type TypeSingle = 'single'
type TypeMulti = 'multi'

const emits = defineEmits(['new-value'])

const props = defineProps({
  label: {
    type: String,
    requied: true
  },
  getItems: {
    type: Function,
    required: true
  },
  items: {
    type: [Object],
    reqired: true
  },
  renderType: {
    type: String as PropType<TypeSingle | TypeMulti>,
    default: 'single'
  },
  // applicable only to 'single' render-type
  allowNew: {
    type: Boolean,
    default: true
  }
})

let loading = ref(false)

let results = computed(() => {
  loading.value = false

  return props.items?.map((item: any) => ({
    ...item,
    _text: item.name
  }))
})

const modelValue = ref([] as IAutomcomplete[])

const updateModelValue = (selected: any) => {
  if (selected) {
    const { _text } = selected

    if (props.renderType === 'single') {
      const exists = modelValue.value.some(({ _text: vText }) => {
        return vText === _text
      })

      if (!exists) {
        modelValue.value.push(selected)
      }
    } else {
      modelValue.value = selected
    }

    emits('new-value', modelValue.value)
  }
}

const search = (q: string) => {
  if (!q) return // prevent making request on focus

  loading.value = true

  props.getItems(q)
}

const addValue = (q: string) => {
  modelValue.value.push({ name: q, _text: q })

  emits('new-value', modelValue.value)
}

const unselectItem = (q: string) => {
  const newVal = modelValue.value.filter((v) => {
    if (v._text !== q) return v
  })

  modelValue.value = newVal

  // TODO: fix `undefined` text dsiplaying in the input box after unselect a value

  emits('new-value', modelValue.value)
}
</script>

<style lang="scss" scoped>
.chip-list {
  margin-top: -1rem;
}
:deep(.chip-label-button) {
  display: flex;
  .chip-icon {
    order: 2;
    &:hover {
      cursor: pointer;
    }
    > svg {
      &:hover {
        cursor: pointer;
      }
    }
  }
  > .label {
    order: 1;
  }
}
</style>
