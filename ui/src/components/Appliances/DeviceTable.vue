<template>
  <div class="device-container">
    <div class="search-filter">
      <FeatherInput v-model="searchValue" type="text" label="Search device" :background="true" />
      <FeatherButton icon="Filter">
        <FeatherIcon :icon="filterIcon" />
      </FeatherButton>
    </div>
    <table class="tl1 tl2 tl3 data-table" summary="Devices" data-test="device-table">
      <thead>
        <tr>
          <th scope="col" data-test="col-device">Device</th>
          <th scope="col" data-test="col-latency">ICMP Latency</th>
          <th scope="col" data-test="col-uptime">SNMP Uptime</th>
          <th scope="col" data-test="col-status">Status</th>
        </tr>
      </thead>
      <TransitionGroup name="data-table" tag="tbody">
        <tr v-for="(device, index) in listDevicesWithBgColor" :key="device.id" :data-index="index" data-test="device-item">
          <td>{{ device.name }}</td>
          <td :class="device.latencyClass">{{ device.icmp_latency }}</td>
          <td :class="device.uptimeClass">{{ device.snmp_uptime }}</td>
          <td :class="device.statusClass">{{ device.status }}</td>
        </tr>
      </TransitionGroup>
    </table>
  </div>
</template>

<script setup lang="ts">
import FilterAlt from '@featherds/icon/action/FilterAlt'
import { useDeviceQueries } from '@/store/Queries/deviceQueries'
import { formatItemBgColor } from '@/helpers/formatting'

const deviceQueries = useDeviceQueries()

const filterIcon = computed(() => markRaw(FilterAlt))
const searchValue = ''

const listDevicesWithBgColor = computed(() => formatItemBgColor(deviceQueries.listDevices))
</script>

<style lang="scss" scoped>
@import "@featherds/styles/themes/variables";

.device-container {
  margin-left: var($spacing-xl)
}
.search-filter {
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin-top: var($spacing-xl);
  > .feather-input-container {
    width: 50%;
  }
  .feather-input-label {
    background-color: red;
    // background-color: var($background);
  }
}
</style>
