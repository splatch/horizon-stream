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
            <FeatherInput
              v-model="formInput.name"
              label="ICMP/SNMP name"
              class="name-input"
            />
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
            :disabled="isFormInvalid"
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
import { IIcon } from '@/types'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'

const enum DiscoverytType {
  None,
  ICSNMP,
  Azure,
  SysLog,
  SNMPTraps
}

const enum ContentEditableType {
  IP,
  community,
  port
}

interface DiscoveryInput {
  type: DiscoverytType
  name: string
  location: string
  IPRange: string
  communityString: string
  UDPPort: number
}

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const isFormShown = ref(true)

const formInput = ref<DiscoveryInput>({
  type: DiscoverytType.ICSNMP,
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
  label: 'Enter IP ranges and/or subnets'
}
const isContentInvalidIP = (args) => {
  console.log('args', args)
}
const contentFormattedIP = (args) => {
  console.log('args', args)
}

const contentEditableCommunityRef = ref()
const community = {
  type: ContentEditableType.community,
  regexDelim: '',
  label: 'Enter community string (optional)'
}
const isContentInvalidCommunity = (args) => {
  console.log('args', args)
}
const contentFormattedCommunity = (args) => {
  console.log('args', args)
}

const contentEditablePortRef = ref()
const port = {
  type: ContentEditableType.port,
  regexDelim: '',
  label: 'Enter UDP port (optional)'
}
const isContentInvalidPort = (args) => {
  console.log('args', args)
}
const contentFormattedPort = (args) => {
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
  // contentEditableIPRef.value.validateAndFormat()
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
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';

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
  h4,
  h5 {
    margin-bottom: var(variables.$spacing-m);
  }
  > form {
    div[class$='-input'] {
      margin-bottom: var(variables.$spacing-m);
    }
  }

  @include mediaQueriesMixins.screen-xl {
    .content-editable-container {
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
