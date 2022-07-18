<template>
  <div class="device-container">
    <div class="search-filter">
      <FeatherInput v-model="searchValue" type="clear" label="Search device..." />
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
        <tr v-for="(device, index) in deviceQueries.listDevices" :key="device.id" :data-index="index" data-test="device-item">
          <td>{{ device.name }}</td>
          <td>{{ device.icmp_latency }}</td>
          <td>{{ device.snmp_uptime }}</td>
          <td>{{ device.status }}</td>
        </tr>
      </TransitionGroup>
    </table>
  </div>
</template>

<script setup lang="ts">
import FilterAlt from '@featherds/icon/action/FilterAlt'
import { useDeviceQueries } from '@/store/Queries/deviceQueries'

const deviceQueries = useDeviceQueries()

const filterIcon = computed(() => markRaw(FilterAlt))
const searchValue = ''
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
}
</style>
