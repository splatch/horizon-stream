<template>
<div class="header">
  Graphs
  <FeatherButton text @click="onDownload">
    Download All
  </FeatherButton>
</div>

<div id="graphs-layout">
  <div class="left-column">
    <LineGraph :graph="minionUptimeResponseTime" />
    <LineGraph :graph="minionUptime" />
    <LineGraph :graph="minionResponseTime" />
    <LineGraph :graph="cpuResponseTotal" />
    <LineGraph :graph="maxFDS" />
  </div>
  <div class="right-column">
    <LineGraph :graph="processResidentMemBytes" />
    <LineGraph :graph="processStartTime" />
    <LineGraph :graph="processVirtualMemBytes" />
    <LineGraph :graph="processVirtualMemMaxBytes" />
    <LineGraph :graph="goThreads" />
  </div>
</div>
</template>
  
<script setup lang="ts">
import { TimeRangeUnit } from '@/types/graphql'
import { GraphProps } from '@/types/graphs'
import { downloadMultipleCanvases } from '@/components/Graphs/utils'

const minionUptimeResponseTime: GraphProps = {
  label: 'Minion Uptime and Minion Response Time',
  metrics: ['minion_uptime_sec', 'response_time_msec'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const minionUptime: GraphProps = {
  label: 'Minion Uptime',
  metrics: ['minion_uptime_sec'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const minionResponseTime: GraphProps = {
  label: 'Minion Response Time',
  metrics: ['response_time_msec'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const cpuResponseTotal: GraphProps = {
  label: 'CPU Seconds Total',
  metrics: ['process_cpu_seconds_total'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const maxFDS: GraphProps = {
  label: 'Max FDS',
  metrics: ['process_max_fds'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const processResidentMemBytes: GraphProps = {
  label: 'Process Resident Mem Bytes',
  metrics: ['process_resident_memory_bytes'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const processStartTime: GraphProps = {
  label: 'Process Start Time',
  metrics: ['process_start_time_seconds'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const processVirtualMemBytes: GraphProps = {
  label: 'Process Virtual Mem Bytes',
  metrics: ['process_virtual_memory_bytes'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const processVirtualMemMaxBytes: GraphProps = {
  label: 'Process Virtual Mem Max Bytes',
  metrics: ['process_virtual_memory_max_bytes'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const goThreads: GraphProps = {
  label: 'Go Threads',
  metrics: ['go_threads'],
  monitor: '',
  // id: 0,
  // instance: '',
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}

const onDownload = () => {
  const page = document.getElementById('graphs-layout') as HTMLElement
  const canvases = document.getElementsByClassName('canvas') as HTMLCollectionOf<HTMLCanvasElement>
  downloadMultipleCanvases(page, canvases)
}
</script>
  
<style scoped lang="scss">
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";

.header {
  @include typography.headline3();
  display: flex;
  padding: 6px;
  background: var(variables.$shade-4);
  justify-content: space-between;
}

#graphs-layout {
  display: flex;
  gap: 20px;
  
  .left-column, 
  .right-column {
    display: flex;
    flex-direction: column;
    width: calc(50% - 10px);
  }
}
</style>
  