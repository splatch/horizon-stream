<template>
  <PageHeader
    heading="Discovery"
    class="header"
  />
  <div class="container">
    <section class="my-discovery">
      <!-- <FeatherButton
        @click="addDiscovery"
        primary
        class="add-btn"
      >
        New discovery
        <template #icon>
          <Icon :icon="addIcon" />
        </template>
      </FeatherButton> -->
      <div>
        <div>
          <div>My active discovery</div>
          <div>You have no active discovery</div>
        </div>
        <div>
          <div>My passive discovery</div>
          <div>You have no active discovery</div>
        </div>
        <!-- dropdown select discovery (edit) -->
        <div>Search/Filter discovery</div>
      </div>
    </section>

    <!-- add/edit a discovery  -->
    <section
      v-if="isFormShown"
      class="discovery"
    >
      <h5>Select a discovery</h5>
      <form>
        <div>
          <!-- active -->
          <!-- passive -->
        </div>
        <div v-if="formInput.type">
          <h4>ICMP/SNMP Discovery Setup</h4>
          <div>
            <!-- ICMP/SNMP name input -->
            <FeatherInput
              v-model="formInput.name"
              label="ICMP/SNMP name"
              class="name-input"
            />
            <!-- location input -->
            <!-- IP input -->
            <div>
              <div class="editable-box">
                <label for="contentEditable">Enter IP ranges and/or subnets</label>
                <div
                  v-html="renderHtml"
                  ref="contentEdited"
                  contenteditable="true"
                  id="contentEditable"
                  class="content-editable"
                />
                <span @click="validateFormatIPs"><Icon :icon="checkCircleIcon" /></span>
              </div>
            </div>
            <!-- community input -->
            <!-- port input -->
          </div>
        </div>
        <div
          v-else
          class="get-started"
        >
          Select a discovery to get started
        </div>
        <div class="footer">
          <FeatherButton
            @click="cancelHandler"
            secondary
            >cancel</FeatherButton
          >
          <FeatherButton
            @click="saveHandler"
            :disabled="isFormInvalid || isEditableContentInvalid"
            primary
            type="submit"
            >save discovery</FeatherButton
          >
        </div>
      </form>
    </section>
  </div>
</template>

<script lang="ts" setup>
import AddIcon from '@featherds/icon/action/Add'
import CheckCircleIcon from '@featherds/icon/action/CheckCircle'
import { IIcon } from '@/types'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import { isIPAddress } from 'ip-address-validator'

const enum DiscoverytType {
  None,
  ICSNMP,
  Azure,
  SysLog,
  SNMPTraps
}

interface DiscoveryInput {
  type: DiscoverytType
  name: string
  location: string
  IPRange: string
  communityString: string
  UDPPort: number
}

const isEditableContentInvalid = ref(true)
const contentEdited = ref()
const renderHtml = ref('')
const validateFormatIPs = () => {
  isEditableContentInvalid.value = validateIPs()
  renderHtml.value = formatIPs()
}
const validateIPs = () => {
  const reDelimiter = /[,;\s]+/
  const IPs = contentEdited.value.textContent.split(reDelimiter)

  return IPs.some((IP: string) => !isIPAddress(IP))
}
const formatIPs = () => {
  const reDelimiter = /[,;\s]+/
  const IPs = contentEdited.value.textContent.split(reDelimiter)

  return IPs.map((IP: string) => {
    if (isIPAddress(IP)) return IP

    return `<span style="color: red">${IP}</span>`
  }).join(';')
}

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const isFormShown = ref(true)

const formInput = ref<DiscoveryInput>({
  type: DiscoverytType.ICSNMP,
  name: '', // required?
  location: 'Default',
  IPRange: '', // required?
  communityString: '', // required?
  UDPPort: 0 // required?
})

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
  // formInput.value.type = DiscoverytType.None
}

const addIcon: IIcon = {
  image: markRaw(AddIcon)
}
const checkCircleIcon: IIcon = {
  image: markRaw(CheckCircleIcon),
  tooltip: 'Validate'
}
</script>

<style lang="scss" scope>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';

.editable-box {
  position: relative;
  > .content-editable {
    border: 1px solid var(variables.$secondary-text-on-surface);
    border-radius: vars.$border-radius-xs;
    padding: var(variables.$spacing-xs) var(variables.$spacing-m) var(variables.$spacing-xl);
    height: 200px;
    overflow: scroll;
    outline: none;
  }
  .feather-icon {
    position: absolute;
    right: 5px;
    bottom: 5px;
    width: 1.5rem;
    height: 1.5rem;
    outline: none;
    &:hover {
      cursor: pointer;
    }
  }
}

.container {
  display: flex;
  flex-direction: row;
  flex-flow: wrap;
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);

  @include mediaQueriesMixins.screen-md {
    column-gap: 3%;
    > .my-discovery {
      width: 30%;
      min-width: auto;
    }
    > .discovery {
      width: 67%;
      min-width: auto;
    }
  }
}

.my-discovery {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 400px;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  margin-bottom: var(variables.$spacing-l);
  > * {
    margin-bottom: var(variables.$spacing-m);
    &:last-child {
      margin-bottom: 0;
    }
  }

  @include mediaQueriesMixins.screen-sm {
    flex-direction: row;
    column-gap: 2%;
    > * {
      width: 32%;
      margin-bottom: 0;
    }
  }
  @include mediaQueriesMixins.screen-md {
    flex-direction: column;
    margin-bottom: 0;
    > * {
      width: 100%;
      margin-bottom: var(variables.$spacing-m);
    }
  }
}

.discovery {
  width: 100%;
  min-width: 400px;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  > h5,
  h4 {
    margin-bottom: var(variables.$spacing-m);
  }
}

.header {
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);
}

.footer {
  display: flex;
  justify-content: flex-end;
}
</style>
