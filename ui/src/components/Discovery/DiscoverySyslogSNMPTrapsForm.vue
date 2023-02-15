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
        <div class="content-editable-container">
          <!-- location -->
          <FeatherInput
            :label="discoveryText.DiscoverySyslogSNMPTrapsForm.location"
            :hint="''"
            v-model="formInput.location"
            class="my-text-input"
          />
          <!-- carousel -->
          <!-- tags -->
          <DiscoveryAutocomplete
            @new-value="tagsAdded"
            :label="discoveryText.DiscoverySyslogSNMPTrapsForm.tag"
            :get-items="discoveryQueries.getTagsUponTyping"
            :items="discoveryQueries.tagsUponTyping"
            render-type="multi"
          />
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
