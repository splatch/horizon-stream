<template>
  <table class="tl1 tl2 tl3" summary="Devices" data-test="device-table">
    <thead>
      <tr>
        <th scope="col" data-test="col-device">Device</th>
        <th scope="col" data-test="col-latency">ICMP Latency</th>
        <th scope="col" data-test="col-uptime">SNMP Uptime</th>
      </tr>
    </thead>
    <TransitionGroup tag="tbody" :css="false" @before-enter="onBeforeEnter" @enter="onEnter" @leave="onLeave">
      <tr v-for="(device, index) in appliancesQueries.listDevices" :key="device.id" :data-index="index"
        data-test="device-item">
        <td>{{ device.name }}</td>
        <td>{{ device.icmp_latency }}</td>
        <td>{{ device.snmp_uptime }}</td>
      </tr>
    </TransitionGroup>
  </table>
</template>

<script setup lang="ts">
import { useAppliancesQueries } from '@/store/Queries/appliancesQueries'
import useTransitionGroup from '@/composables/useTransitionGroup'

const appliancesQueries = useAppliancesQueries()
const { onBeforeEnter, onEnter, onLeave } = useTransitionGroup()
</script>

<style lang="scss">
@import "@featherds/table/scss/table";

table {
  @include table;
}
</style>

