<template>
  <FeatherAutocomplete
    :model-value="modelValue"
    @update:model-value="updateModelValue"
    @search="search"
    @new="addValue"
    :loading="loading"
    :results="results"
    :label="(props.label as string)"
    :type="props.renderType"
    allow-new
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
  renderType: {
    type: String as PropType<TypeSingle | TypeMulti>,
    default: 'single'
  },
  allowNew: {
    type: Boolean,
    default: false
  },
  getItems: {
    type: Function,
    required: true
  },
  items: {
    type: [Object],
    reqired: true
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

watch(modelValue.value, (newVal, oldVal) => {
  // TODO: watch not trigger when remove item from modelValue nor when readd a removed item
  emits('new-value', newVal)
})

const updateModelValue = (selected: any) => {
  if (selected) {
    const { _text } = selected

    if (props.renderType === 'single') {
      const exists = modelValue.value?.some(({ _text: vText }) => {
        return vText === _text
      })

      if (!exists) modelValue.value?.push(selected)
    } else {
      modelValue.value = selected
    }
  }
}

const search = (q: string) => {
  if (!q) return // prevent making request on focus

  loading.value = true

  props.getItems(q)
}

const addValue = (q: string) => {
  // TODO: request endpoint to add new value, or only when saving the form
  // modelValue.value?.push({ name: q, _text: q })
  modelValue.value = [...modelValue.value, { name: q, _text: q }]

  emits('new-value', modelValue.value) // TODO: emit here since watch is not triggered when remove item from modelValue
}

const unselectItem = (q: string) => {
  const newVal = modelValue.value.filter((v) => {
    if (v._text !== q) return v
  })

  modelValue.value = [...newVal]

  emits('new-value', modelValue.value) // TODO: emit here since watch is not triggered when remove item from modelValue
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
