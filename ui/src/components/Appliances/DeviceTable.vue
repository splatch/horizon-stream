<template>
  <table class="tl1 tl2 tl3" summary="Devices">
    <thead>
      <tr>
        <th scope="col">Device</th>
        <th scope="col">ICMP Latency</th>
        <th scope="col">SNMP Uptime</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="device in store.gDeviceItems" :key="device.id">
        <td>{{ device.name }}</td>
        <td>{{ device.icmp_latency }}</td>
        <td>{{ device.snmp_uptime }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import { onMounted }from 'vue'
import { useDeviceStore } from '@/store/deviceStore'

const store = useDeviceStore()

onMounted(() => {
  store.aGetDevices()
})
</script>

<style lang="scss">
@import "@featherds/table/scss/table";
table {
  @include table;
}
</style>

