<template>
  <form class="form">
    <h5>{{ discoveryText.Discovery.heading1 }}</h5>
    <form>
      <div>
        <!-- active -->
        <!-- passive -->
      </div>
      <div v-if="formInput.type">
        <h4>{{ discoveryText.Discovery.heading2 }}</h4>
        <div>
          <!-- ICMP/SNMP name input -->
          <FeatherInput
            v-model="formInput.name"
            :label="discoveryText.Discovery.nameInputLabel"
            class="name-input"
          />
          <LocationsAutocomplete />
          <!-- location input -->
          <div class="content-editable-container">
            <DiscoveryContentEditable
              @is-content-invalid="isContentInvalidIP"
              @content-formatted="contentFormattedIP"
              ref="contentEditableIPRef"
              :contentType="IPs.type"
              :regexDelim="IPs.regexDelim"
              :label="IPs.label"
              class="ip-input"
            />
            <DiscoveryContentEditable
              @is-content-invalid="isContentInvalidCommunity"
              @content-formatted="contentFormattedCommunity"
              ref="contentEditableCommunityRef"
              :contentType="community.type"
              :regexDelim="community.regexDelim"
              :label="community.label"
              class="community-input"
            />
            <DiscoveryContentEditable
              @is-content-invalid="isContentInvalidPort"
              @content-formatted="contentFormattedPort"
              ref="contentEditablePortRef"
              :contentType="port.type"
              :regexDelim="port.regexDelim"
              :label="port.label"
              class="port-input"
            />
          </div>
        </div>
      </div>
      <div
        v-else
        class="get-started"
      >
        {{ discoveryText.Discovery.nodiscoverySelectedMsg }}
      </div>
      <div class="footer">
        <FeatherButton
          @click="cancelHandler"
          secondary
          >{{ discoveryText.Discovery.button.cancel }}</FeatherButton
        >
        <FeatherButton
          @click="saveHandler"
          :disabled="isFormInvalid"
          primary
          type="submit"
          >{{ discoveryText.Discovery.button.submit }}</FeatherButton
        >
      </div>
    </form>
  </form>
</template>

<script lang="ts" setup>
import { DiscoveryInput } from '@/types/discovery'
import { ContentEditableType, DiscoveryType } from '@/components/Discovery/discovery.constants'
import discoveryText from '@/components/Discovery/discovery.text'
const isFormShown = ref(true)

const formInput = ref<DiscoveryInput>({
  type: DiscoveryType.ICMP,
  name: '',
  location: 'Default',
  IPRange: '',
  communityString: '', // optional
  UDPPort: 0 // optional
})

const contentEditableIPRef = ref()
const IPs = {
  type: ContentEditableType.IP,
  regexDelim: '[,; ]+',
  label: discoveryText.ContentEditable.IP.label
}
const isContentInvalidIP = (args: boolean) => {
  console.log('args', args)
}
const contentFormattedIP = (args: string) => {
  console.log('args', args)
}

const contentEditableCommunityRef = ref()
const community = {
  type: ContentEditableType.Community,
  regexDelim: '',
  label: discoveryText.ContentEditable.CommunityString.label
}
const isContentInvalidCommunity = (args: boolean) => {
  console.log('args', args)
}
const contentFormattedCommunity = (args: string) => {
  console.log('args', args)
}

const contentEditablePortRef = ref()
const port = {
  type: ContentEditableType.Port,
  regexDelim: '',
  label: discoveryText.ContentEditable.UDPPort.label
}
const isContentInvalidPort = (args: boolean) => {
  console.log('args', args)
}
const contentFormattedPort = (args: string) => {
  console.log('args', args)
}

const addDiscovery = () => {
  isFormShown.value = true
}

const isFormInvalid = computed(() => {
  // formInput validation
  return false
})

const saveHandler = () => {
  // startSpinner()
  // add query
  // if success
  // stopSpinner()
  // if error
  // showSnackbar({
  // msg: 'Save unsuccessfully!'
  // })
}

const cancelHandler = () => {
  formInput.value.type = DiscoveryType.None
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';
> form {
  div[class$='-input'] {
    margin-bottom: var(variables.$spacing-m);
  }
}
.content-editable-container {
  display: flex;
  flex-direction: column;
  gap: var(variables.$spacing-m);
  @include mediaQueriesMixins.screen-md {
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

.footer {
  display: flex;
  justify-content: flex-end;
}

.content-editable-container {
  display: flex;
}
</style>
