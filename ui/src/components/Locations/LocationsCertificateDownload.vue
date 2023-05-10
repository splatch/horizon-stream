<template>
  <div
    class="download-wrapper"
    data-test="locations-download-cert"
  >
    <div class="download-container">
      <div class="download-title">{{ props.title }}</div>
      <div class="download-functionality">
        <FeatherButton
          primary
          @click="onButtonClick"
          data-test="download-btn"
        >
          {{ props.buttonText }}
        </FeatherButton>
        <div class="input-wrapper">
          <FeatherInput
            :label="props.inputPlaceholder"
            :modelValue="props.inputModel"
            type="text"
            class="download-input"
            :disabled="true"
            data-test="download-input"
          />
          <FeatherButton
            :disabled="!props.inputModel"
            text
            @click="copyClick"
            class="download-copy-button"
          >
            <template v-slot:icon>
              <FeatherIcon
                :icon="CopyIcon"
                aria-hidden="true"
                focusable="false"
              />
              Copy
            </template>
          </FeatherButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import CopyIcon from '@featherds/icon/action/ContentCopy'
import useSnackbar from '@/composables/useSnackbar'
const { showSnackbar } = useSnackbar()

const props = defineProps({
  title: {
    default: 'Certificate Status',
    type: String
  },
  buttonText: {
    default: 'Download Certificate',
    type: String
  },
  onButtonClick: {
    required: true,
    type: Function as PropType<(event: Event) => void>,
    default: () => ({})
  },
  inputPlaceholder: {
    default: 'Certificate Password',
    type: String
  },
  inputModel: {
    required: true,
    type: String
  }
})

const copyClick = () => {
  navigator.clipboard.writeText(props.inputModel)
  showSnackbar({
    msg: 'Copied.'
  })
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.download-wrapper {
  width: 100%;
}
.download-container {
  background-color: var(variables.$border-light-on-warning);
  display: flex;
  flex-direction: column;
  border-radius: 4px;
  padding: var(variables.$spacing-l);
}
.download-functionality {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
  width: 100%;
  gap: var(variables.$spacing-l);

  :deep(.btn-primary) {
    max-width: 285px;
  }
}
.input-wrapper {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex: 1 1 0;
  gap: var(variables.$spacing-xs);
}
.download-title {
  @include typography.subtitle2();
  margin-bottom: var(variables.$spacing-m);
}
.download-copy-button {
  min-width: 87px;
}
.download-input {
  width: 285px;
}
:deep(.feather-input-sub-text) {
  display: none;
}
</style>
