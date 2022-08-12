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
        v-model="device.label"
      />
      
      <!-- Location name -->
      <FeatherInput
        data-test="location-name-input"
        label="Location"
        v-model="device.location"
      />
      
      <!-- Monitoring area -->
      <FeatherInput
        data-test="monitoring-area-input"
        label="Monitoring Area"
        v-model="device.monitoringArea"
      />

      <!-- Management IP -->
      <FeatherInput
        data-test="ip-input"
        label="Management IP"
        v-model="device.managementIp"
      />

      <!-- Community String -->
      <FeatherInput
        data-test="string-input"
        label="Community String (Optional)"
        v-model="device.snmpCommunityString"
      />

      <!-- Port -->
      <FeatherInput
        type="number"
        data-test="port-input"
        label="Port (Optional)"
        v-model="device.port"
      />
      
      <!-- Latitude -->
      <FeatherInput
        type="number"
        data-test="port-latitude"
        label="Latitude (Optional)"
        v-model="device.latitude"
      />
      
      <!-- Longitude -->
      <FeatherInput
        type="number"
        data-test="port-longitude"
        label="Longitude (Optional)"
        v-model="device.longitude"
      />
    </template>

    <template v-slot:footer>
      <FeatherButton 
        data-test="cancel-btn" 
        secondary 
        @click="cancel">
          Cancel
      </FeatherButton>
      
      <FeatherButton 
        data-test="save-btn" 
        primary
        :disabled="!device.label || !device.location || !device.managementIp || !device.monitoringArea" 
        @click="save">
          Save
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script setup lang="ts">
import Add from '@featherds/icon/action/Add'
import { useDeviceMutations } from '@/store/Mutations/deviceMutations'
import { useApplianceQueries } from '@/store/Queries/applianceQueries'
import useModal from '@/composables/useModal'
import useSnackbar from '@/composables/useSnackbar'
import { DeviceCreateDTOInput } from '@/types/appliances'

const { showSnackbar } = useSnackbar()
const { openModal, closeModal, isVisible } = useModal()
const deviceMutations = useDeviceMutations()
const applianceQueries = useApplianceQueries()

const defaultDevice: DeviceCreateDTOInput = { 
  label: '',
  location: '',
  latitude: null,
  longitude: null,
  monitoringArea: '',
  managementIp: '',
  port: null,
  snmpCommunityString: ''
}

const device: DeviceCreateDTOInput = reactive({ ...defaultDevice })

const save = async () => {
  // payload of following number type fields require null as value (form field return empty string if value entered then erased)
  Object.assign(device, {
    port: !device.port?.length ? null : device.port,
    latitude: !device.latitude?.length ? null : device.latitude,
    longitude: !device.longitude?.length ? null : device.longitude
  })

  await deviceMutations.addDevice({ device })
  
  if (!deviceMutations.error) {
    // clears device obj on successful save
    Object.assign(device, defaultDevice)

    closeModal()

    showSnackbar({
      msg: 'Device successfuly saved.'
    })

    // Timeout because device may not be available right away
    // TODO: Replace timeout with websocket/polling
    setTimeout(() => {
      applianceQueries.fetchDevicesForTable()
    }, 350)
  }
}

const cancel = () => {
  // clears device obj
  Object.assign(device, defaultDevice)

  closeModal()
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