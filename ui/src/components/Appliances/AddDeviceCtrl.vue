<template>
  <FeatherButton 
    primary
    data-test="add-device-btn" 
    @click="openModal"
  > 
    <template v-slot:icon>
      <FeatherIcon 
        :icon="Add" 
        aria-hidden="true" 
        focusable="false" 
      />
      Add Device
    </template>
  </FeatherButton>

  <PrimaryModal title="Add Device" :visible="isVisible">
    <template v-slot:content>
      <!-- Device Name -->
      <FeatherInput
        data-test="name-input"
        label="Name"
        v-model="device.name"
      />

      <!-- Management IP -->
      <FeatherInput
        data-test="ip-input"
        label="Management IP"
        v-model="device.management_ip"
      />

      <!-- Community String -->
      <FeatherInput
        data-test="string-input"
        label="Community String (Optional)"
        v-model="device.community_string"
      />

      <!-- Port -->
      <FeatherInput
        type="number"
        data-test="port-input"
        label="Port (Optional)"
        v-model="device.port"
      />
    </template>

    <template v-slot:footer>
      <FeatherButton 
        data-test="cancel-btn" 
        secondary 
        @click="closeModal">
          Cancel
      </FeatherButton>
      
      <FeatherButton 
        data-test="save-btn" 
        primary
        :disabled="!device.name || !device.management_ip"
        @click="save">
          Save
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script setup lang="ts">
import Add from '@featherds/icon/action/Add'
import { useDeviceMutations } from '@/store/Mutations/deviceMutations'
import useModal from '@/composables/useModal'
import useSnackbar from '@/composables/useSnackbar'

const { showSnackbar } = useSnackbar()
const { openModal, closeModal, isVisible } = useModal()
const deviceMutations = useDeviceMutations()

const defaultDevice = {
  name: undefined,
  management_ip: undefined,
  community_string: undefined,
  port: undefined
}

const device = reactive(defaultDevice)

const save = async () => {
  await deviceMutations.saveDevice({ device })


  if (!deviceMutations.error) {
    // clears device obj on successful save
    Object.assign(device, defaultDevice)

    closeModal()

    showSnackbar({
      msg: 'Device successfuly saved.'
    })
  }
}
</script>

<style scoped lang="scss">
@import "@featherds/styles/mixins/typography";

.title {
  @include headline3;
  text-align: center;
  margin-bottom: 15px;
}
</style>