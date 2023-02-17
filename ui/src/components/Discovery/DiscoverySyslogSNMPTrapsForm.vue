<template>
  <div class="syslog-snmp-traps-form">
    <div class="headline-action">
      <div class="headline">{{ DiscoverySyslogSNMPTrapsForm.headline }}</div>
      <!-- <Icon
        @click="deleteHandler"
        :icon="deleteIcon"
      /> -->
    </div>
    <form @submit.prevent="submitHandler">
      <div class="form-content">
        <!-- location -->
        <LocationsAutocomplete @location-selected="locationsSelectedListener" />
        <DiscoveryHelpConfiguring />
        <DiscoveryAutocomplete
          @items-selected="tagsSelectedListener"
          :get-items="discoveryQueries.getTagsUponTyping"
          :items="discoveryQueries.tagsUponTyping"
          :label="DiscoverySyslogSNMPTrapsForm.tag"
        />
        <div class="content-editable-container">
          <DiscoveryContentEditable
            @is-content-invalid="isCommunityStringInvalidListerner"
            @content-formatted="communityStringEnteredListerner"
            ref="contentEditableCommunityStringRef"
            :contentType="communityString.type"
            :regexDelim="communityString.regexDelim"
            :label="communityString.label"
            :default-content="'someCommunityString'"
            class="community-string-input"
          />
          <DiscoveryContentEditable
            @is-content-invalid="isUDPPortInvalidListener"
            @content-formatted="UDPPortEnteredListener"
            ref="contentEditableUDPPortRef"
            :contentType="udpPort.type"
            :regexDelim="udpPort.regexDelim"
            :label="udpPort.label"
            :default-content="'someUDPPort'"
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
  <DiscoverySuccessModal ref="successModalRef" />
</template>

<script lang="ts" setup>
import DeleteIcon from '@featherds/icon/action/Delete'
import { IIcon } from '@/types'
import discoveryText from './discovery.text'
import { DiscoverySyslogSNMPTrapsForm } from './discovery.text'
import { ContentEditableType } from './discovery.constants'
import { useDiscoveryQueries } from '@/store/Queries/discoveryQueries'

const emits = defineEmits(['cancel-editing'])

const discoveryQueries = useDiscoveryQueries()

const isFormInvalid = ref(
  computed(() => {
    return isCommunityStringInvalid || isUDPPortInvalid
  })
)

let tagsSelected: Record<string, string>[] = []
const tagsSelectedListener = (tags: Record<string, string>[]) => {
  tagsSelected = tags
}

let locationsSelected: Record<string, string>[] = []
const locationsSelectedListener = (locations: Record<string, string>[]) => {
  locationsSelected = locations
}

const contentEditableCommunityStringRef = ref()
const communityString = {
  type: ContentEditableType.CommunityString,
  regexDelim: '',
  label: discoveryText.ContentEditable.CommunityString.label
}
let isCommunityStringInvalid = false
const isCommunityStringInvalidListerner = (isInvalid: boolean) => {
  isCommunityStringInvalid = isInvalid
}
let communityStringEntered = ''
const communityStringEnteredListerner = (str: string) => {
  communityStringEntered = str
}

const contentEditableUDPPortRef = ref()
const udpPort = {
  type: ContentEditableType.UDPPort,
  regexDelim: '',
  label: discoveryText.ContentEditable.UDPPort.label
}
let isUDPPortInvalid = false
const isUDPPortInvalidListener = (isInvalid: boolean) => {
  isUDPPortInvalid = isInvalid
}
let UDPPortEntered = ''
const UDPPortEnteredListener = (str: string) => {
  UDPPortEntered = str
}

const successModalRef = ref()
const isSyslogSNMPTrapsFormSaveSuccess = computed(() => discoveryQueries.isSyslogSNMPTrapsFormSaveSuccess)
watch(isSyslogSNMPTrapsFormSaveSuccess, (success: boolean) => {
  if (success) successModalRef.value.openSuccessModal()
})
const submitHandler = () => {
  contentEditableCommunityStringRef.value.validateAndFormat()
  contentEditableUDPPortRef.value.validateAndFormat()

  discoveryQueries.saveSyslogSNMPTrapsForm({
    locations: locationsSelected,
    tagsSelected: tagsSelected,
    communityString: communityStringEntered,
    UDPPort: UDPPortEntered
  })
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

.form-content {
  > * {
    margin-bottom: var(variables.$spacing-l);
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.discovery-autocomplete {
  width: 100%;

  @include mediaQueriesMixins.screen-xl {
    width: 49%;
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
