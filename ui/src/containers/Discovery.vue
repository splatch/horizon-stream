<template>
  <PageHeader
    heading="Discovery"
    class="header"
  />
  <!-- add discovery button -->
  <!-- TODO: Awaiting UI confirmation to have the button at top right corner -->
  <!-- <FeatherButton
        @click="addDiscovery"
        primary
      >
        New discovery
        <template #icon>
          <Icon :icon="addIcon" />
        </template>
      </FeatherButton> -->
  <div class="container">
    <!-- my discovery -->
    <section class="my-discovery">
      <!-- my active -->
      <div>
        <div>My active discovery</div>
        <div>You have no active discovery</div>
      </div>
      <!-- my passive -->
      <div>
        <div>My passive discovery</div>
        <div>You have no active discovery</div>
      </div>
      <!-- dropdown select discovery (edit) -->
      <div>Search/Filter discovery</div>
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
            :disabled="isFormValid"
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
  name: '', // required?
  location: 'Default',
  IPRange: '', // required?
  communityString: '', // required?
  UDPPort: 0 // required?
})

const addDiscovery = () => {
  isFormShown.value = true
}

const isFormValid = computed(() => {
  // formInput validation
  return true
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
  formInput.value.type = DiscoverytType.None
}

const addIcon: IIcon = {
  image: markRaw(AddIcon)
}
</script>

<style lang="scss" scope>
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
