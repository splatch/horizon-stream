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
        @is-content-invalid="isIPRangeInvalidHandler"
        @content-formatted="store.snmp.IPRange"
        ref="contentEditableIPRef"
        :contentType="IPs.type"
        :regexDelim="IPs.regexDelim"
        :label="IPs.label"
        class="ip-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="(value: boolean) => isContentValid('communityString', value)"
        @content-formatted="(value: string) => saveContent('communityString', value)"
        ref="contentEditableCommunityStringRef"
        :contentType="community.type"
        :regexDelim="community.regexDelim"
        :label="community.label"
        class="community-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="(value: boolean) => isContentValid('UDPPort', value)"
        @content-formatted="(value: string) => saveContent('UDPPort', value)"
        ref="contentEditableUDPPortRef"
        :contentType="port.type"
        :regexDelim="port.regexDelim"
        :label="port.label"
        class="port-input"
      />
    </div>

    <div class="footer">
      <FeatherButton
        @click="$emit('close-form')"
        secondary
        >{{ discoveryText.Discovery.button.cancel }}</FeatherButton
      >
      <ButtonWithSpinner
        :disabled="isDisabled"
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
import { ContentEditableType, DiscoveryType } from '@/components/Discovery/discovery.constants'
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

const discovery = ref<DiscoveryInput>(store.snmp)
const contentEditableCommunityStringRef = ref()
const contentEditableUDPPortRef = ref()
const isIPRangeInvalid = ref(false)
const selectLocation = (location: Required<Location[]>) =>
  location[0] && location[0].location && store.selectLocation(location[0].location, true)

const isDisabled = computed(() => !store.snmp.name || !store.selectedLocations.length || isIPRangeInvalid.value)

const contentEditableIPRef = ref()
const IPs = {
  type: ContentEditableType.IP,
  regexDelim: '[,; ]+',
  label: discoveryText.ContentEditable.IP.label
}

const isContentValid = (property: string, val: boolean) => {
  console.log('valid - ', property, val)
}

const saveContent = (property: string, val: string) => {
  discovery.value[property] = val
}

const isIPRangeInvalidHandler = (value: boolean) => {
  isIPRangeInvalid.value = value
  console.log(isIPRangeInvalid.value)
}

const community = {
  type: ContentEditableType.CommunityString,
  regexDelim: '',
  label: discoveryText.ContentEditable.CommunityString.label
}

const port = {
  type: ContentEditableType.UDPPort,
  regexDelim: '',
  label: discoveryText.ContentEditable.UDPPort.label
}

const saveHandler = async () => {
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
