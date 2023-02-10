<template>
  <form class="form">
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
</template>

<script lang="ts" setup>
import { IDiscoverySNMPInput } from '@/types/discovery'
import { DiscoveryType } from './discovery.constants'

const formInput = ref<IDiscoverySNMPInput>({
  type: DiscoveryType.SNMP,
  name: '', // required?
  location: 'Default',
  IPRange: '', // required?
  communityString: '', // required?
  UDPPort: 0 // required?
})

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
  formInput.value.type = null
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';

.form {
  h5,
  h4 {
    margin-bottom: var(variables.$spacing-m);
  }
}

.footer {
  display: flex;
  justify-content: flex-end;
}
</style>
