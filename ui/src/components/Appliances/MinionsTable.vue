<template>
  <table class="tl1 tl2 tl3 data-table" summary="Minions" data-test="minions-table">
    <thead>
      <tr>
        <th scope="col" data-test="col-date">Date</th>
        <th scope="col" data-test="col-minion">Minion</th>
        <th scope="col" data-test="col-latency">Latency</th>
        <th scope="col" data-test="col-uptime">Uptime</th>
      </tr>
    </thead>
    <TransitionGroup name="data-table" tag="tbody">
      <tr v-for="(minion, index) in listMinionsWithBgColor" :key="minion.id" :data-index="index" data-test="minion-item">
        <td>{{ minion.date }}</td>
        <td>{{ minion.label }}</td>
        <td :class="minion.latencyClass">{{ minion.icmp_latency }}</td>
        <td :class="minion.uptimeClass">{{ minion.snmp_uptime }}</td>
      </tr>
    </TransitionGroup>
  </table>
</template>

<script setup lang="ts">
import { useMinionsQueries } from '@/store/Queries/minionsQueries'
import { formatItemBgColor } from '@/helpers/formatting'

const minionsQueries = useMinionsQueries()

const listMinionsWithBgColor = computed(() => formatItemBgColor(minionsQueries.listMinions))
</script>
