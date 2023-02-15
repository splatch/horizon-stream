<template>
  <div class="syslog-snmp-traps-form">
    <div class="headline-action">
      <div class="headline">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.headline }}</div>
      <!-- <Icon
        @click="deleteHandler"
        :icon="deleteIcon"
      /> -->
    </div>
    <form @submit.prevent="submitHandler">
      <div class="form-iputs">
        <!-- location -->
        <!-- <DiscoveryAutocomplete
            @new-value="locationAdded"
            :label="discoveryText.DiscoverySyslogSNMPTrapsForm.location"
            :get-items="discoveryQueries.getTagsUponTyping"
            :items="discoveryQueries.tagsUponTyping"
          /> -->
        <!-- help -->
        <div class="help">
          <div class="subtitle1">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.heading }}</div>
          <ul class="type-square">
            <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.list.text1 }}</li>
            <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.list.text2 }}</li>
            <li
              @click="isHelpOpen = !isHelpOpen"
              class="step-opener"
            >
              <span>
                {{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.list.text3 }}
                <FeatherIcon :icon="isHelpOpen ? expandLessIcon : expandMoreIcon" />
              </span>
            </li>
          </ul>
          <div
            v-if="isHelpOpen"
            class="steps"
          >
            <div class="stepper">
              <div
                @click="stepHandler(0)"
                class="arrow"
                :class="step === 1 ? 'disabled' : ''"
              >
                <FeatherIcon :icon="chevronLeftIcon" />
              </div>
              <div class="step-line">
                <div class="line"></div>
                <ul>
                  <li
                    @click="stepHandler(1)"
                    :class="step === 1 ? 'selected' : ''"
                  >
                    1
                  </li>
                  <li
                    @click="stepHandler(2)"
                    :class="step === 2 ? 'selected' : ''"
                  >
                    2
                  </li>
                  <li
                    @click="stepHandler(3)"
                    :class="step === 3 ? 'selected' : ''"
                  >
                    3
                  </li>
                  <li
                    @click="stepHandler(4)"
                    :class="step === 4 ? 'selected' : ''"
                  >
                    4
                  </li>
                </ul>
              </div>
              <div
                @click="stepHandler(5)"
                class="arrow"
                :class="step === 4 ? 'disabled' : ''"
              >
                <FeatherIcon :icon="chevronRightIcon" />
              </div>
            </div>
            <div
              v-if="step === 1"
              class="step-content"
            >
              <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.heading }}</div>
              <ul class="type-square">
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.list.text1 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.list.text2 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.list.text3 }}</li>
              </ul>
            </div>
            <div
              v-if="step === 2"
              class="step-content"
            >
              <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.heading }}</div>
              <ul class="type-square">
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.list.text1 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.list.text2 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.list.text3 }}</li>
              </ul>
            </div>
            <div
              v-if="step === 3"
              class="step-content"
            >
              <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.heading }}</div>
              <ul class="type-square">
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.list.text1 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.list.text2 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.list.text3 }}</li>
              </ul>
            </div>
            <div
              v-if="step === 4"
              class="step-content"
            >
              <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.heading }}</div>
              <ul class="type-square">
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.list.text1 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.list.text2 }}</li>
                <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.list.text3 }}</li>
              </ul>
            </div>
          </div>
        </div>
        <!-- tags -->
        <DiscoveryAutocomplete
          @new-value="tagsAdded"
          :label="discoveryText.DiscoverySyslogSNMPTrapsForm.tag"
          :get-items="discoveryQueries.getTagsUponTyping"
          :items="discoveryQueries.tagsUponTyping"
        />
        <div class="content-editable-container">
          <DiscoveryContentEditable
            @is-content-invalid="isContentInvalidCommunityString"
            @content-formatted="contentFormattedCommunityString"
            ref="contentEditableCommunityStringRef"
            :contentType="communityString.type"
            :regexDelim="communityString.regexDelim"
            :label="communityString.label"
            class="community-string-input"
          />
          <DiscoveryContentEditable
            @is-content-invalid="isContentInvalidUDPPort"
            @content-formatted="contentFormattedUDPPort"
            ref="contentEditableUDPPortRef"
            :contentType="udpPort.type"
            :regexDelim="udpPort.regexDelim"
            :label="udpPort.label"
            class="udp-port-input"
          />
        </div>
      </div>
      <div class="form-footer">
        <FeatherButton
          @click="emits('cancel-editing')"
          secondary
          >{{ discoveryText.Discovery.button.cancel }}</FeatherButton
        >
        <FeatherButton
          :disabled="isFormInvalid"
          primary
          type="submit"
          >{{ discoveryText.Discovery.button.submit }}</FeatherButton
        >
      </div>
    </form>
  </div>
</template>

<script lang="ts" setup>
import DeleteIcon from '@featherds/icon/action/Delete'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import { IIcon } from '@/types'
import discoveryText from './discovery.text'
import { ContentEditableType } from './discovery.constants'
import { useDiscoveryQueries } from '@/store/Queries/discoveryQueries'
import ExpandMoreIcon from '@featherds/icon/navigation/ExpandMore'
import ExpandLessIcon from '@featherds/icon/navigation/ExpandLess'
import ChevronLeftIcon from '@featherds/icon/navigation/ChevronLeft'
import ChevronRightIcon from '@featherds/icon/navigation/ChevronRight'

interface FormInput {
  location: string
  tag: string
  communtityString: string
  UPDPort: string
}

const emits = defineEmits(['cancel-editing'])

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const discoveryQueries = useDiscoveryQueries()

const isFormInvalid = ref(true)

const formInput = ref<FormInput>({
  location: '',
  tag: '',
  communtityString: '', // optional
  UPDPort: '' // optional
})

const tagsAdded = (tags: Record<string, string>[]) => {
  console.log('emit - tagsAdded', tags)
}

const step = ref(1)
const isHelpOpen = ref(false)
const stepHandler = (s: number) => {
  switch (s) {
    case 0: // previous
      if (step.value !== 1) step.value = step.value - 1
      break
    case 5: // next
      if (step.value !== 4) step.value = step.value + 1
      break
    default:
      step.value = s
  }
}

const contentEditableCommunityStringRef = ref()
const communityString = {
  type: ContentEditableType.CommunityString,
  regexDelim: '',
  label: discoveryText.ContentEditable.CommunityString.label
}
const isContentInvalidCommunityString = (args) => {
  console.log('args', args)
}
const contentFormattedCommunityString = (args) => {
  console.log('args', args)
}

const contentEditableUDPPortRef = ref()
const udpPort = {
  type: ContentEditableType.UDPPort,
  regexDelim: '',
  label: discoveryText.ContentEditable.UDPPort.label
}
const isContentInvalidUDPPort = (args) => {
  console.log('args', args)
}
const contentFormattedUDPPort = (args) => {
  console.log('args', args)
}

const submitHandler = () => {
  // startSpinner()
  // add query
  // if success
  // stopSpinner()
  // if error
  // showSnackbar({
  // msg: 'Save unsuccessfully!'
  // })
}

const deleteHandler = () => {
  console.log('delete')
}

const deleteIcon: IIcon = {
  image: markRaw(DeleteIcon),
  tooltip: 'Delete',
  size: '1.5rem',
  cursorHover: 'pointer'
}
const expandMoreIcon = markRaw(ExpandMoreIcon)
const expandLessIcon = markRaw(ExpandLessIcon)
const chevronLeftIcon = markRaw(ChevronLeftIcon)
const chevronRightIcon = markRaw(ChevronRightIcon)
</script>

<style lang="scss" scoped>
@use '@featherds/styles/mixins/typography';
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';

.headline-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(variables.$spacing-m);
}

.headline {
  @include typography.headline3();
}

.help {
  border-left: 1px solid var(variables.$border-on-surface);
  padding-left: 1rem;
  .subtitle1 {
    @include typography.subtitle1();
    margin-bottom: 1rem;
  }
  .subtitle2 {
    @include typography.subtitle2();
    margin-bottom: 1rem;
  }
  ul.type-square > li {
    list-style-type: square;
  }
  > ul.type-square {
    margin-left: 1.5rem;
    margin-bottom: 1rem;
  }
  .step-opener {
    > span {
      color: var(variables.$secondary-variant);
      &:hover {
        cursor: pointer;
      }
      .feather-icon {
        width: 1.2rem;
        height: 1.2rem;
        vertical-align: bottom;
      }
    }
  }
  .steps {
    margin-left: 2rem;
    .stepper {
      display: flex;
      margin-bottom: 1rem;
      .arrow {
        width: 10%;
        text-align: center;
        &:hover {
          cursor: pointer;
        }
      }
      .step-line {
        width: 80%;
        position: relative;
        .line {
          border: 1px solid var(variables.$border-on-surface);
          background-color: var(variables.$background);
          width: 100%;
          position: absolute;
          top: 1rem;
          left: 0;
          z-index: 1000;
        }
        > ul {
          display: flex;
          justify-content: space-between;
          position: relative;
          z-index: 1001;
          > li {
            border: 1px solid var(variables.$border-on-surface);
            border-radius: 50%;
            width: 2rem;
            height: 2rem;
            text-align: center;
            padding-top: 2px;
            color: var(variables.$secondary-text-on-surface);
            background-color: var(variables.$surface);
            &:hover {
              cursor: pointer;
            }
          }
        }
        .selected {
          background-color: var(variables.$background);
        }
      }
      .disabled {
        color: var(variables.$disabled-text-on-surface);
        &:hover {
          cursor: default;
        }
      }
      .feather-icon {
        width: 1.2rem;
        height: 1.2rem;
        vertical-align: bottom;
      }
    }
    .step-content {
      margin-right: 2rem;
      margin-left: 2rem;
      > ul {
        margin-left: 1rem;
      }
    }
  }
}

.content-editable-container {
  > div[class$='-input'] {
    margin-bottom: var(variables.$spacing-m);
  }

  @include mediaQueriesMixins.screen-xl {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-end;
    > div {
      width: 49%;
    }
  }
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(variables.$border-on-surface);
  padding-top: var(variables.$spacing-m);
}
</style>
