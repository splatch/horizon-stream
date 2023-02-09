<template>
  <div class="content-editable-wrapper">
    <label for="contentEditable">{{ props.label }}</label>
    <div
      v-html="htmlString"
      ref="contentEditableRef"
      contenteditable="true"
      id="contentEditable"
      class="content-editable"
    />
    <span
      v-if="props.regexDelim"
      @click="validateAndFormat"
      class="validate-format"
      ><Icon :icon="checkCircleIcon"
    /></span>
  </div>
</template>

<script lang="ts" setup>
import { isIPAddress } from 'ip-address-validator'
import CheckCircleIcon from '@featherds/icon/action/CheckCircle'
import { IIcon } from '@/types'
import { ContentEditableType } from '@/components/Discovery/discovery.constants'
import { PropType } from 'vue'

const emit = defineEmits(['is-content-invalid', 'content-formatted'])

const props = defineProps({
  contentType: {
    type: Number as PropType<ContentEditableType>,
    required: true
  },
  regexDelim: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: ''
  }
})

const isContentInvalid = ref(true)
const contentEditableRef = ref()
const htmlString = ref('')

const validateAndFormat = () => {
  isContentInvalid.value = validateContent()
  htmlString.value = formatContent()

  emit('is-content-invalid', isContentInvalid.value)
  if (!isContentInvalid.value) emit('content-formatted', htmlString.value)
}

const validateContent = () => {
  const regexDelim = new RegExp(props.regexDelim)
  const contentEditableStrings = contentEditableRef.value.textContent.split(regexDelim)
  let isValid = true

  switch (props.contentType) {
    case ContentEditableType.IP:
      isValid = contentEditableStrings.some((str: string) => !isIPAddress(str))
      break
    case ContentEditableType.Community:
      break
    case ContentEditableType.Port:
      break
    default:
  }

  return isValid
}

const formatContent = () => {
  const regexDelim = new RegExp(props.regexDelim)
  const contentEditableStrings = contentEditableRef.value.textContent.split(regexDelim)
  let formattedStr = ''
  let errorStr = '<span style="color: #ff555e">{{}}</span>'

  switch (props.contentType) {
    case ContentEditableType.IP:
      formattedStr = contentEditableStrings
        .map((str: string) => {
          if (isIPAddress(str)) return str

          return errorStr.replace('{{}}', str)
        })
        .join(';')
      break
    case ContentEditableType.Community:
      break
    case ContentEditableType.Port:
      break
    default:
  }

  return formattedStr
}

const checkCircleIcon: IIcon = {
  image: markRaw(CheckCircleIcon),
  tooltip: 'Validate'
}

defineExpose({
  validateAndFormat
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';

.content-editable-wrapper {
  position: relative;
  > .content-editable {
    border: 1px solid var(variables.$secondary-text-on-surface);
    border-radius: vars.$border-radius-xs;
    padding: var(variables.$spacing-xs) var(variables.$spacing-m) var(variables.$spacing-xl);
    height: 200px;
    overflow: scroll;
    outline: none;
    > .error {
      color: red;
    }
  }
  > .validate-format {
    position: absolute;
    right: 5px;
    bottom: 5px;
    :deep(> .feather-icon) {
      width: 1.5rem;
      height: 1.5rem;
      outline: none;
      &:hover {
        cursor: pointer;
      }
    }
  }
}
</style>
