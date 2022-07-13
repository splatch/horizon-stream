<template>
  <table class="tl1 tl2 tl3" summary="Devices" data-test="device-table">
    <thead>
      <tr>
        <th scope="col" data-test="col-device">Device</th>
        <th scope="col" data-test="col-latency">ICMP Latency</th>
        <th scope="col" data-test="col-uptime">SNMP Uptime</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="device in appliancesQueries.listDevices" :key="device.id" data-test="device-item">
        <td>{{ device.name }}</td>
        <td>{{ device.icmp_latency }}</td>
        <td>{{ device.snmp_uptime }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import { useAppliancesQueries } from '@/store/Queries/appliancesQueries'
import useSpinner from '@/composables/useSpinner'

const appliancesQueries = useAppliancesQueries()
const { startSpinner, stopSpinner} = useSpinner()

onMounted( () => {
  startSpinner()
  setTimeout(async () => {
    await appliancesQueries.fetch()
    stopSpinner()
  }, 350)
})
</script>

<style lang="scss">
@import "@featherds/table/scss/table";

table {
  @include table;
}
</style>

