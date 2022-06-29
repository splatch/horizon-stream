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
      <tr v-for="device in devices" :key="device.id">
        <td>{{ device.name }}</td>
        <td>{{ device.icmp_latency }}</td>
        <td>{{ device.snmp_uptime }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import { Device } from '@/types/appliances'
const devices = computed<Device[]>(() => [])
</script>

<style lang="scss">
@import "@featherds/table/scss/table";
table {
  @include table;
}
</style>

