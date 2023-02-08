<template>
  <PageHeader
    heading="Discovery"
    class="header"
  />
  <div class="container">
    <section class="my-discovery">
      <div class="add-btn">
        <FeatherButton
          @click="addDiscovery"
          primary
        >
          New discovery
          <template #icon>
            <Icon :icon="addIcon" />
          </template>
        </FeatherButton>
      </div>
      <div class="my-discovery-inner">
        <!-- dropdown select discovery (edit) -->
        <div>Search/Filter discovery</div>
        <div class="card-my-discoveries">
          <div class="title">
            My Active Discovery
            <div class="count">0</div>
          </div>
          <div class="emtpy">
            <FeatherIcon
              :icon="Warning"
              class="icon"
            />You have no active discovery
          </div>
        </div>
        <div class="card-my-discoveries">
          <div class="title">My Passive Discovery</div>
          <div class="emtpy">
            <FeatherIcon
              :icon="Warning"
              class="icon"
            />You have no active discovery
          </div>
        </div>
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
import Warning from '@featherds/icon/notification/Warning'

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
@use '@featherds/styles/mixins/typography';

.container {
  display: flex;
  flex-direction: column;
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);

  @include mediaQueriesMixins.screen-md {
    column-gap: 1.4%;
    flex-direction: row;
  }
}

.my-discovery {
  width: 100%;
  border-bottom: 1px solid var(--feather-border-on-surface);
  margin-bottom: var(variables.$spacing-l);

  .add-btn {
    width: 100%;
    margin-bottom: var(variables.$spacing-l);
    border-bottom: 1px solid var(variables.$border-on-surface);
    > button {
      margin-bottom: var(variables.$spacing-l);
    }
  }

  > .my-discovery-inner {
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-bottom: var(variables.$spacing-l);
    > .card-my-discoveries {
      background-color: var(variables.$surface);
      border: 1px solid var(variables.$border-on-surface);
      border-radius: vars.$border-radius-s;
      padding: var(variables.$spacing-s);
      margin-bottom: var(variables.$spacing-m);
      min-height: 100px;
      &:last-child {
        margin-bottom: 0;
      }
    }
    @include mediaQueriesMixins.screen-md {
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
    width: 25%;
    min-width: 300px;
    > * {
      width: 100%;
      margin-bottom: var(variables.$spacing-m);
    }
  }
}

.discovery {
  width: 100%;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  background-color: var(variables.$surface);
  > h5,
  h4 {
    margin-bottom: var(variables.$spacing-m);
  }
  @include mediaQueriesMixins.screen-md {
    margin-bottom: 0;
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

.title {
  @include typography.subtitle1;
  display: flex;

  .count {
    background-color: #00666d1f;
    padding: 0 var(variables.$spacing-xs);
    margin-left: var(variables.$spacing-m);
    border-radius: 5px;
    @include typography.body-small;
  }
}

.emtpy {
  display: flex;
  gap: 8px;
  margin-top: var(variables.$spacing-s);

  .icon {
    width: 24px;
    height: 24px;
    color: var(variables.$shade-1);
  }
}
</style>
