<template>
  <div class="form">
    <div class="form-title">{{ DiscoverySNMPForm.title }}</div>
    <FeatherInput
      v-model="store.snmp.name"
      :label="DiscoverySNMPForm.nameInputLabel"
      class="name-input"
    />
    <LocationsAutocomplete
      class="select"
      type="single"
      :preLoadedlocations="props.discovery?.location"
      @location-selected="selectLocation"
    />
    <!--<DiscoveryAutocomplete
      class="select"
      @items-selected="store.snmp.tags"
      :get-items="discoveryQueries.getTagsUponTyping"
      :items="discoveryQueries.tagsUponTyping"
      :label="DiscoverySNMPForm.tag"
    /> -->
    <div class="content-editable-container">
      <DiscoveryContentEditable
        @is-content-invalid="isIPRangeInvalidListener"
        @content-formatted="ipRangeEnteredListerner"
        :contentType="IPs.type"
        :regexDelim="IPs.regexDelim"
        :label="IPs.label"
        default-content="127.0.0.1"
        ref="contentEditableIPRef"
        class="ip-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="isCommunityStringInvalidListerner"
        @content-formatted="communityStringEnteredListerner"
        :contentType="community.type"
        :regexDelim="community.regexDelim"
        :label="community.label"
        default-content="someCommunityString"
        ref="contentEditableCommunityStringRef"
        class="community-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="isUDPPortInvalidListener"
        @content-formatted="UDPPortEnteredListener"
        :contentType="udpPort.type"
        :regexDelim="udpPort.regexDelim"
        :label="udpPort.label"
        :default-content="1234"
        class="udp-port-input"
        ref="contentEditableUDPPortRef"
      />
    </div>

    <div class="footer">
      <FeatherButton
        @click="$emit('close-form')"
        secondary
        >{{ discoveryText.Discovery.button.cancel }}</FeatherButton
      >
      <ButtonWithSpinner
        @click="saveHandler"
        primary
      >
        {{ discoveryText.Discovery.button.submit }}
      </ButtonWithSpinner>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { DiscoveryInput } from '@/types/discovery'
import { ContentEditableType } from '@/components/Discovery/discovery.constants'
import discoveryText, { DiscoverySNMPForm } from '@/components/Discovery/discovery.text'
import { useDiscoveryStore } from '@/store/Views/discoveryStore'
import { Location } from '@/types/graphql'

import useSnackbar from '@/composables/useSnackbar'
const { showSnackbar } = useSnackbar()

const emit = defineEmits(['close-form'])

const store = useDiscoveryStore()
const props = defineProps<{
  discovery?: DiscoveryInput | null
  successCallback: (name: string) => void
}>()

const contentEditableCommunityStringRef = ref()
const contentEditableUDPPortRef = ref()
// const isIPRangeInvalid = ref(false)
const selectLocation = (location: Required<Location[]>) =>
  location[0] && location[0].location && store.selectLocation(location[0].location, true)

// const isContentValid = (property: string, val: boolean) => {
//   console.log('valid - ', property, val)
// }

const contentEditableIPRef = ref()
const IPs = {
  type: ContentEditableType.IP,
  regexDelim: '[,; ]+',
  label: discoveryText.ContentEditable.IP.label
}
let isIPRangeInvalid = false
const isIPRangeInvalidListener = (isInvalid: boolean) => {
  isIPRangeInvalid = isInvalid
}
const ipRangeEnteredListerner = (str: string) => {
  const regexDelim = new RegExp(udpPort.regexDelim)
  store.setIpAddresses(str.split(regexDelim))
}

const community = {
  type: ContentEditableType.CommunityString,
  regexDelim: '',
  label: discoveryText.ContentEditable.CommunityString.label
}
let isCommunityStringInvalid = false
const isCommunityStringInvalidListerner = (isInvalid: boolean) => {
  isCommunityStringInvalid = isInvalid
}
const communityStringEnteredListerner = (str: string) => {
  const regexDelim = new RegExp(udpPort.regexDelim)
  store.setCommunityString(str.split(regexDelim))
}

const udpPort = {
  type: ContentEditableType.UDPPort,
  regexDelim: '[,; ]+',
  label: discoveryText.ContentEditable.UDPPort.label
}
let isUDPPortInvalid = false
const isUDPPortInvalidListener = (isInvalid: boolean) => {
  isUDPPortInvalid = isInvalid
}
const UDPPortEnteredListener = (str: string) => {
  const regexDelim = new RegExp(udpPort.regexDelim)
  const ports = str.split(regexDelim)
  store.setUdpPorts(ports.map((p) => parseInt(p)))
}

const saveHandler = async () => {
  contentEditableIPRef.value.validateAndFormat()
  contentEditableCommunityStringRef.value.validateAndFormat()
  contentEditableUDPPortRef.value.validateAndFormat()

  const success = await store.saveDiscoverySnmp()
  if (success) {
    store.clearSnmpForm()
    emit('close-form')
    props.successCallback(store.snmp.name)
  } else {
    showSnackbar({
      msg: discoveryText.Discovery.error.errorCreate
    })
  }
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';
.form {
  .form-title {
    @include typography.headline4;
    margin-bottom: var(variables.$spacing-m);
  }
  .select {
    margin-top: var(variables.$spacing-xl);
    margin-bottom: var(variables.$spacing-s);
    width: 40%;
  }
}

.footer {
  display: flex;
  justify-content: flex-end;
}

.content-editable-container {
  display: flex;
  flex-direction: column;
  > div {
    margin-bottom: var(variables.$spacing-l);
  }
  @include mediaQueriesMixins.screen-xl {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-end;
    > div {
      width: 32%;
    }
  }
}

:deep(.feather-input-sub-text) {
  display: none !important;
}
</style>
