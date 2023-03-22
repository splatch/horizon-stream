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
  <div>
    <div class="content-editable-wrapper">
      <div class="label">
        <label for="contentEditable">{{ props.label }}</label>
        <FeatherTooltip
          v-if="props.tooltipText"
          :title="props.tooltipText"
          v-slot="{ attrs, on }"
        >
          <FeatherButton
            v-bind="attrs"
            v-on="on"
            icon="info"
            class="icon-help"
            ><FeatherIcon :icon="Help"> </FeatherIcon
          ></FeatherButton>
        </FeatherTooltip>
      </div>
      <div
        v-html="htmlString"
        @keyup="contentChange"
        ref="contentEditableRef"
        contenteditable="true"
        id="contentEditable"
        class="content-editable"
        @blur="validateContent"
      />
      <span
        v-if="props.regexDelim && isContentNotEmpty"
        @click="validateContent"
        class="validate-format"
        ><Icon :icon="checkCircleIcon"
      /></span>
    </div>
    <div
      v-if="isContentInvalid"
      class="errorMsgBox"
    >
      {{ errorMsg }}
    </div>
  </div>
</template>

<script lang="ts" setup>
import CheckCircleIcon from '@featherds/icon/action/CheckCircle'
import { IIcon } from '@/types'
import { ContentEditableType } from '@/components/Discovery/discovery.constants'
import { PropType } from 'vue'
import { fncArgVoid } from '@/types'
import discoveryText from '@/components/Discovery/discovery.text'

import Help from '@featherds/icon/action/Help'

const emit = defineEmits(['content-formatted'])
const props = defineProps({
  contentType: {
    type: Number as PropType<ContentEditableType>,
    required: true
  },
  regexDelim: {
    type: String,
    default: ''
  },
  regexExpression: {
    type: Array<string>
  },
  label: {
    type: String,
    default: ''
  },
  defaultContent: {
    type: [String, Number],
    default: ''
  },
  content: {
    type: [String, Number],
    default: ''
  },
  tooltipText: {
    type: String
  },
  isRequired: {
    type: Boolean,
    required: false
  }
})

const isContentNotEmpty = ref(false)
const contentEditableRef = ref()
const isContentInvalid = ref(false)
const errorMsg = ref('')
const htmlString = ref(props.content || props.defaultContent) // to render string as html

watch(props, () => {
  htmlString.value = props.content
})

const contentChange = () => {
  isContentNotEmpty.value = contentEditableRef.value.textContent.length as boolean
}

const splitContent = (str: string[]): (string | number)[] | null => {
  if (props.contentType === ContentEditableType.UDPPort) {
    return str.map((p) => parseInt(p))
  }
  return str
}

const formatContent = (contentEditableStrings, regexp) => {
  const ERROR_STR = '<span style="color: #ff555e">{{}}</span>'

  return contentEditableStrings
    .map((str: string) => {
      if (regexp.map((t) => t.test(str)).includes(true)) {
        return str
      } else {
        isContentInvalid.value = true
        return ERROR_STR.replace('{{}}', str)
      }
    })
    .join(';')
}

const validateContent = () => {
  const regexDelim = new RegExp(props.regexDelim)
  const contentEditableStrings = contentEditableRef.value.textContent.replace(/\s/g, '').split(regexDelim)
  const regexp = props.regexExpression?.map((r) => new RegExp(r))
  isContentInvalid.value = false

  if (regexp) {
    const formattedString = formatContent(contentEditableStrings, regexp)
    htmlString.value = formattedString
  }

  if (!isContentInvalid.value) {
    emit('content-formatted', splitContent(contentEditableStrings))
  } else {
    showErrorMsg()
  }
  return isContentInvalid.value
}

const showErrorMsg = () => {
  if (isContentInvalid.value) {
    if (!isContentNotEmpty.value && props.isRequired) {
      errorMsg.value = discoveryText.ContentEditable.errorRequired
    } else {
      errorMsg.value = discoveryText.ContentEditable.errorInvalidValue
    }
  }
}

const reset: fncArgVoid = () => {
  contentEditableRef.value.textContent = props.defaultContent
  errorMsg.value = ''
  isContentInvalid.value = false
}

const checkCircleIcon: IIcon = {
  image: markRaw(CheckCircleIcon),
  tooltip: 'Validate'
}

defineExpose({
  validateContent,
  reset
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';

.content-editable-wrapper {
  // TODO: mimic FeatherDS input component; label inside input box and animate it to be on top on the box when focus
  position: relative;
  > .label {
    display: flex;
    justify-content: space-between;
    align-items: center;
    > .icon-help {
      height: 20px;
      color: var(variables.$secondary-variant);
      width: 20px;
      min-width: 20px;
      border: none;
      padding: 0;
      > svg {
        font-size: 1.2rem;
      }
    }
  }
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
      color: var(variables.$success);
      &:hover {
        cursor: pointer;
      }
    }
  }
}
.errorMsgBox {
  height: 24px;
  @include typography.caption;
  color: var(variables.$error);
  padding-top: var(variables.$spacing-xs);
}
</style>
