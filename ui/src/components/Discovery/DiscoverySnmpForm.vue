<template>
  <div class="form">
    <div class="form-title">{{ discoveryText.Discovery.headline2 }}</div>
    <FeatherInput
      v-model="formInput.name"
      :label="discoveryText.Discovery.nameInputLabel"
      class="name-input"
    />
    <LocationsAutocomplete
      class="location"
      type="single"
      :preLoadedlocations="formInput.location"
      @location-selected="handleLocations"
    />
    <div class="content-editable-container">
      <DiscoveryContentEditable
        @is-content-invalid="(value) => isValid('IPRange', value)"
        @content-formatted="(value) => saveContent('IPRange', value)"
        ref="contentEditableIPRef"
        :contentType="IPs.type"
        :regexDelim="IPs.regexDelim"
        :label="IPs.label"
        class="ip-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="(value) => isValid('communityString', value)"
        @content-formatted="(value) => saveContent('communityString', value)"
        ref="contentEditableCommunityRef"
        :contentType="community.type"
        :regexDelim="community.regexDelim"
        :label="community.label"
        class="community-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="(value) => isValid('UDPPort', value)"
        @content-formatted="(value) => saveContent('UDPPort', value)"
        ref="contentEditablePortRef"
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
      <FeatherButton
        @click="saveHandler"
        :disabled="isFormInvalid"
        primary
        >{{ discoveryText.Discovery.button.submit }}</FeatherButton
      >
    </div>
  </div>
</template>

<script lang="ts" setup>
import { DiscoveryInput } from '@/types/discovery'
import { ContentEditableType, DiscoveryType } from '@/components/Discovery/discovery.constants'
import discoveryText from '@/components/Discovery/discovery.text'
import { useDiscoveryStore } from '@/store/Views/discoveryStore'
import { IDiscovery } from '@/types/discovery'

const emit = defineEmits(['close-form'])
const store = useDiscoveryStore()

const props = defineProps<{
  discovery?: IDiscovery
}>()

const formInput = ref<DiscoveryInput>({
  type: DiscoveryType.ICMP,
  name: props.discovery?.name || '',
  location: props.discovery?.location || [1],
  IPRange: props.discovery?.IPRange || '',
  communityString: props.discovery?.communityString || '', // optional
  UDPPort: props.discovery?.UDPPort || '' // optional
})

watch(props, () => {
  formInput.value = {
    type: DiscoveryType.ICMP,
    name: props.discovery?.name || '',
    location: props.discovery?.location || [1],
    IPRange: props.discovery?.IPRange || '',
    communityString: props.discovery?.communityString || '', // optional
    UDPPort: props.discovery?.UDPPort || '' // optional
  }
})

const contentEditableIPRef = ref()
const IPs = {
  type: ContentEditableType.IP,
  regexDelim: '[,; ]+',
  label: discoveryText.ContentEditable.IP.label
}
const isContentValid = (property: string, val: boolean) => {
  console.log(property, val)
}

const contentEditableCommunityRef = ref()
const community = {
  type: ContentEditableType.CommunityString,
  regexDelim: '',
  label: discoveryText.ContentEditable.CommunityString.label
}

const contentEditablePortRef = ref()
const port = {
  type: ContentEditableType.UDPPort,
  regexDelim: '',
  label: discoveryText.ContentEditable.UDPPort.label
}

const saveContent = (property: string, val: string) => {
  formInput.value[property] = val
}

const handleLocations = (locations: Location[]) => {
  formInput.location = locations.map((l: Location) => l.id)
}

const isFormInvalid = computed(() => {
  // formInput validation
  return false
})

const saveHandler = () => {
  store.saveDiscovery(formInput.value)
  emit('close-form')
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';
.form {
  .form-title {
    @include typography.headline4();
    margin-bottom: var(variables.$spacing-m);
  }
  .location {
    margin-top: var(variables.$spacing-xl);
    margin-bottom: var(variables.$spacing-s);
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
</style>
