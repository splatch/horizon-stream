<template>
  <form
    @submit.prevent="saveHandler"
    novalidate
    class="form"
  >
    <div class="form-title">{{ DiscoverySNMPForm.title }}</div>
    <FeatherInput
      v-model="discoveryInfo.configName"
      :label="DiscoverySNMPForm.nameInputLabel"
      class="name-input"
    />
    <LocationsAutocomplete
      class="locations-select"
      type="single"
      :preLoadedlocations="props.discovery?.location"
      @location-selected="setLocation"
    />
    <!--<DiscoveryAutocomplete
      @items-selected="tagsSelectedListener"
      :get-items="discoveryQueries.getTagsSearch"
      :items="discoveryQueries.tagsSearched"
      :label="Common.tagsInput"
      ref="tagsAutocompleteRef"
      data-test="tags-autocomplete"
    />-->
    <div class="content-editable-container">
      <DiscoveryContentEditable
        @is-content-invalid="isIPRangeInvalidListener"
        @content-formatted="(val) => setSnmpConfig('ipAddresses', val)"
        :contentType="ContentEditableType.IP"
        :regexDelim="IP_RANGE.regexDelim"
        :label="discoveryText.ContentEditable.IP.label"
        ref="contentEditableIPRef"
        class="ip-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="isCommunityStringInvalidListerner"
        @content-formatted="(val) => setSnmpConfig('snmpConfig.readCommunities', val)"
        :contentType="ContentEditableType.CommunityString"
        :regexDelim="COMMUNITY_STRING.regexDelim"
        :label="discoveryText.ContentEditable.CommunityString.label"
        :default-content="COMMUNITY_STRING.default"
        ref="contentEditableCommunityStringRef"
        class="community-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="isUDPPortInvalidListener"
        @content-formatted="(val) => setSnmpConfig('snmpConfig.ports', parseInt(val))"
        :contentType="ContentEditableType.UDPPort"
        :regexDelim="UDP_PORT.regexDelim"
        :label="discoveryText.ContentEditable.UDPPort.label"
        :default-content="UDP_PORT.default"
        class="udp-port-input"
        ref="contentEditableUDPPortRef"
      />
    </div>

    <div class="footer">
      <FeatherButton
        @click="cancel"
        secondary
        >{{ discoveryText.Discovery.button.cancel }}</FeatherButton
      >
      <ButtonWithSpinner
        type="submit"
        primary
        :disabled="isDisabled"
        :isFetching="isFetchingSnmp"
      >
        {{ discoveryText.Discovery.button.submit }}
      </ButtonWithSpinner>
    </div>
  </form>
</template>

<script lang="ts" setup>
import { ContentEditableType, UDP_PORT, COMMUNITY_STRING, IP_RANGE } from '@/components/Discovery/discovery.constants'
import discoveryText, { DiscoverySNMPForm } from '@/components/Discovery/discovery.text'
import { useDiscoveryQueries } from '@/store/Queries/discoveryQueries'
import { Location, DiscoveryConfig } from '@/types/graphql'
import { set } from 'lodash'
import useSnackbar from '@/composables/useSnackbar'
import { useDiscoveryMutations } from '@/store/Mutations/discoveryMutations'

const { createDiscoveryConfig, errorSnmp, isFetchingSnmp } = useDiscoveryMutations()
const { showSnackbar } = useSnackbar()
// const emit = defineEmits(['close-form'])

const discoveryQueries = useDiscoveryQueries()
const props = defineProps<{
  discovery?: DiscoveryConfig | null
  successCallback: (name: string) => void
  cancel: () => void
}>()

const discoveryInfo = ref<DiscoveryConfig>(props.discovery || {})
//const selectedTags = ref<string[]>()
const contentEditableIPRef = ref([])
const contentEditableCommunityStringRef = ref()
const contentEditableUDPPortRef = ref()
const isDisabled = computed(() => !discoveryInfo.value.configName || !discoveryInfo.value.location)

watch(props, () => {
  discoveryInfo.value = props.discovery || {}
})

const setLocation = (location: Location[]) => {
  discoveryInfo.value.location = location[0]?.location
}

const setSnmpConfig = (property: string, val: string | number) => {
  set(discoveryInfo.value, property, [val])
}

//const tagsAutocompleteRef = ref()
// const tagsSelectedListener = (tags: Record<string, string>[]) => {
//   selectedTags.value = tags.map((tag) => tag.name)
// }

let isIPRangeInvalid = false
const isIPRangeInvalidListener = (isInvalid: boolean) => {
  isIPRangeInvalid = isInvalid
}
const ipRangeEnteredListerner = (str: string) => {
  //const regexDelim = new RegExp(udpPort.regexDelim)
  //store.setIpAddresses(str.split(regexDelim))
}

// const community = {
//   type: ContentEditableType.CommunityString,
//   regexDelim: '',
//   label: discoveryText.ContentEditable.CommunityString.label
// }
let isCommunityStringInvalid = false
const isCommunityStringInvalidListerner = (isInvalid: boolean) => {
  isCommunityStringInvalid = isInvalid
}
const communityStringEnteredListerner = (str: string) => {
  //const regexDelim = new RegExp(udpPort.regexDelim)
  //store.setCommunityString(str.split(regexDelim))
}

let isUDPPortInvalid = false
const isUDPPortInvalidListener = (isInvalid: boolean) => {
  isUDPPortInvalid = isInvalid
}
const UDPPortEnteredListener = (str: string) => {
  // const regexDelim = new RegExp(udpPort.regexDelim)
  // const ports = str.split(regexDelim)
  // store.setUdpPorts(ports.map((p) => parseInt(p)))
}

const resetContentEditable = () => {
  // tagsAutocompleteRef.value.reset()
  contentEditableIPRef.value.reset()
  contentEditableCommunityStringRef.value.reset()
  contentEditableUDPPortRef.value.reset()
}

const saveHandler = async () => {
  contentEditableIPRef.value.validateAndFormat()
  contentEditableCommunityStringRef.value.validateAndFormat()
  contentEditableUDPPortRef.value.validateAndFormat()
  await createDiscoveryConfig({ snmpInfo: discoveryInfo.value })
  console.info(errorSnmp)
  if (!errorSnmp.value) {
    discoveryQueries.getDiscoveries()
    resetContentEditable()
    props.successCallback(discoveryInfo.value.configName)
    discoveryInfo.value = {}
  } else {
    showSnackbar({
      msg: errorSnmp.value.response.body.errors[0].message || discoveryText.Discovery.error.errorCreate
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
  .name-input {
    width: 100%;
  }
  .locations-select {
    margin-top: var(variables.$spacing-xl);
    margin-bottom: var(variables.$spacing-s);
    width: 100%;
  }

  @include mediaQueriesMixins.screen-lg {
    .name-input,
    .locations-select {
      width: 49%;
    }
  }
}

.footer {
  display: flex;
  justify-content: flex-end;
}

.discovery-autocomplete {
  :deep(.chip-list) {
    margin-top: var(variables.$spacing-s);
    margin-bottom: var(variables.$spacing-s);
  }
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
