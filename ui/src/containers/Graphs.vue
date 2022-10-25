<template>
<div class="header">Graphs</div>

<div class="graphs-layout">
  <div class="left-column">
    <Graph :graph="minionUptimeResponseTime" />
    <Graph :graph="minionUptime" />
    <Graph :graph="minionResponseTime" />
    <Graph :graph="cpuResponseTotal" />
    <Graph :graph="maxFDS" />
  </div>
  <div class="right-column">
    <Graph :graph="processResidentMemBytes" />
    <Graph :graph="processStartTime" />
    <Graph :graph="processVirtualMemBytes" />
    <Graph :graph="processVirtualMemMaxBytes" />
    <Graph :graph="goThreads" />
  </div>
</div>
</template>
  
<script setup lang="ts">
import { GraphMetric } from '@/types/graphs'

const minionUptimeResponseTime: GraphMetric = {
  label: 'Minion Uptime and Minion Response Time',
  metrics: ['minion_uptime_sec', 'minion_response_time_msec'],
  id: '',
  location: ''
}
const minionUptime: GraphMetric = {
  label: 'Minion Uptime',
  metrics: ['minion_uptime_sec'],
  id: '',
  location: ''
}
const minionResponseTime: GraphMetric = {
  label: 'Minion Response Time',
  metrics: ['minion_response_time_msec'],
  id: '',
  location: ''
}
const cpuResponseTotal: GraphMetric = {
  label: 'CPU Seconds Total',
  metrics: ['process_cpu_seconds_total'],
  id: '',
  location: ''
}
const maxFDS: GraphMetric = {
  label: 'Max FDS',
  metrics: ['process_max_fds'],
  id: '',
  location: ''
}
const processResidentMemBytes: GraphMetric = {
  label: 'Process Resident Mem Bytes',
  metrics: ['process_resident_memory_bytes'],
  id: '',
  location: ''
}
const processStartTime: GraphMetric = {
  label: 'Process Start Time',
  metrics: ['process_start_time_seconds'],
  id: '',
  location: ''
}
const processVirtualMemBytes: GraphMetric = {
  label: 'Process Virtual Mem Bytes',
  metrics: ['process_virtual_memory_bytes'],
  id: '',
  location: ''
}
const processVirtualMemMaxBytes: GraphMetric = {
  label: 'Process Virtual Mem Max Bytes',
  metrics: ['process_virtual_memory_max_bytes'],
  id: '',
  location: ''
}
const goThreads: GraphMetric = {
  label: 'Go Threads',
  metrics: ['go_threads'],
  id: '',
  location: ''
}
</script>
  
<style scoped lang="scss">
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";

.header {
  @include typography.headline3();
  text-align: center;
  padding: 6px;
  background: var(variables.$shade-4);
}

.graphs-layout {
  display: flex;
  
  .left-column, 
  .right-column {
    display: flex;
    flex-direction: column;
    width: 50%;
  }
}
</style>
  