<!-- 
  This component contains:
    - a label.
    - an input box, where text can be typed and/or pasted in.
    - a clickable icon to validae the text entered.

  A required type [IP, Community, Port] prop.
  An optional regex expression for validation.

  Note:
    - the clickable icon is shown if:
        - regexDelim prop is not empty and
        - the input box is not empty
 -->
<template>
  <div class="content-editable-wrapper">
    <label for="contentEditable">{{ props.label }}</label>
    <div
      v-html="htmlString"
      @keyup="contentChange"
      ref="contentEditableRef"
      contenteditable="true"
      id="contentEditable"
      class="content-editable"
    />
    <span
      v-if="props.regexDelim && isContentNotEmpty"
      @click="validateAndFormat"
      class="validate-format"
      ><Icon :icon="checkCircleIcon"
    /></span>
  </div>
</template>

<script lang="ts" setup>
import ipRegex from 'ip-regex'
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
const isContentNotEmpty = ref(false)
const contentEditableRef = ref()
const htmlString = ref('')

const contentChange = () => {
  isContentNotEmpty.value = contentEditableRef.value.textContent.length as boolean
}

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
      isValid = contentEditableStrings.some((str: string) => !ipRegex({ exact: true }).test(str))
      break
    case ContentEditableType.CommunityString:
      break
    case ContentEditableType.UDPPort:
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
          if (ipRegex({ exact: true }).test(str)) return str

          return errorStr.replace('{{}}', str)
        })
        .join(';')
      break
    case ContentEditableType.CommunityString:
      break
    case ContentEditableType.UDPPort:
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
  // TODO: mimic FeatherDS input component; label inside input box and animate it to be on top on the box when focus
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
    bottom: 0;
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
