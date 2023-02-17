<template>
  <form
    @submit="onSubmit"
    class="form"
  >
    <div class="form-title">{{ discoveryText.Discovery.headline2 }}</div>
    <FeatherInput
      v-model="formInput.name"
      :label="discoveryText.Discovery.nameInputLabel"
      class="name-input"
    />
    <LocationsAutocomplete
      class="select"
      type="single"
      :preLoadedlocations="props.discovery?.location"
      @location-selected="handleLocations"
    />
    <DiscoveryAutocomplete
      class="select"
      @items-selected="tagsSelected"
      :get-items="discoveryQueries.getTagsUponTyping"
      :items="discoveryQueries.tagsUponTyping"
      :label="DiscoverySNMPForm.tag"
    />
    <div class="content-editable-container">
      <DiscoveryContentEditable
        @is-content-invalid="(value: boolean) => isContentValid('IPRange', value)"
        @content-formatted="(value: string) => saveContent('IPRange', value)"
        ref="contentEditableIPRef"
        :contentType="IPs.type"
        :regexDelim="IPs.regexDelim"
        :label="IPs.label"
        class="ip-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="(value: boolean) => isContentValid('communityString', value)"
        @content-formatted="(value: string) => saveContent('communityString', value)"
        ref="contentEditableCommunityRef"
        :contentType="community.type"
        :regexDelim="community.regexDelim"
        :label="community.label"
        class="community-input"
      />
      <DiscoveryContentEditable
        @is-content-invalid="(value: boolean) => isContentValid('UDPPort', value)"
        @content-formatted="(value: string) => saveContent('UDPPort', value)"
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
      <ButtonWithSpinner
        :disabled="isDisabled"
        @click="saveAzureDiscovery"
        primary
      >
        {{ discoveryText.Discovery.button.submit }}
      </ButtonWithSpinner>
    </div>
  </form>
</template>

<script lang="ts" setup>
import { DiscoveryInput } from '@/types/discovery'
import { ContentEditableType, DiscoveryType } from '@/components/Discovery/discovery.constants'
import discoveryText, { DiscoverySNMPForm } from '@/components/Discovery/discovery.text'
import { useDiscoveryStore } from '@/store/Views/discoveryStore'
import { Location } from '@/types/graphql'
import { useDiscoveryQueries } from '@/store/Queries/discoveryQueries'

const discoveryQueries = useDiscoveryQueries()

const emit = defineEmits(['close-form'])
const store = useDiscoveryStore()
const props = defineProps<{
  discovery?: DiscoveryInput | null
}>()
const formInput = ref<DiscoveryInput>({
  id: null,
  type: DiscoveryType.ICMP,
  name: '',
  location: [],
  tags: [],
  IPRange: '',
  communityString: '', // optional
  UDPPort: null // optional
})
const setFormInput = () => {
  formInput.value = {
    type: DiscoveryType.ICMP,
    id: props.discovery?.id || 0,
    name: props.discovery?.name || '',
    location: props.discovery?.location || [],
    tags: props.discovery?.tags || [],
    IPRange: props.discovery?.IPRange || '',
    communityString: props.discovery?.communityString || '',
    UDPPort: props.discovery?.UDPPort || null
  }
}

onMounted(() => {
  setFormInput()
})
watch(props, () => {
  setFormInput()
})

const contentEditableIPRef = ref()
const IPs = {
  type: ContentEditableType.IP,
  regexDelim: '[,; ]+',
  label: discoveryText.ContentEditable.IP.label
}
const isContentValid = (property: string, val: boolean) => {
  console.log('valid - ', property, val)
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
  console.log('content - ', property, val)
}

const handleLocations = (locations: Location[]) => {
  formInput.value.location = locations.map((l: Location) => l.id) as string[]
}

const tagsSelected = (tags: Record<string, string>[]) => {
  formInput.value.tags = tags.map((t: Location) => t.id) as string[]
}

const isFormInvalid = computed(() => {
  // formInput validation
  return false
})

const saveHandler = () => {
  //store.saveDiscovery(formInput.value)
  //contentEditableIPRef.validateAndFormat()
  contentEditableCommunityRef.value.validateAndFormat()
  //emit('close-form')
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
</style>
