<template>
  <div class="header-container">
    <HeadlinePage
      text="Graphs"
      data-test="page-header"
    />
    <FeatherButton
      text
      @click="onDownload"
      class="btn-download"
    >
      Download All
    </FeatherButton>
  </div>
  <div
    id="graphs-container"
    v-if="store.fetchIsDone"
  >
    <LineGraph :graph="nodeLatency" />
    <LineGraph :graph="bytesInOut" />
    <LineGraph :graph="bytesIn" />
    <LineGraph :graph="bytesOut" />
  </div>
</template>

<script setup lang="ts">
import { TimeRangeUnit } from '@/types/graphql'
import { GraphProps } from '@/types/graphs'
import { downloadMultipleCanvases } from '@/components/Graphs/utils'
import { useRoute } from 'vue-router'
import { useGraphsQueries } from '@/store/Queries/graphsQueries'
import { AZURE_SCAN } from '@/types'

const route = useRoute()
const store = useGraphsQueries()
const instance = computed(() => {
  const snmpPrimaryIpAddress = store.node.ipInterfaces?.filter(({ snmpPrimary }) => snmpPrimary === true)[0]?.ipAddress
  return store.node.scanType === AZURE_SCAN ? `azure-node-${store.node.id}` : snmpPrimaryIpAddress!
})

const nodeLatency = computed<GraphProps>(() => {
  return {
    label: 'ICMP Response Time',
    metrics: ['response_time_msec'],
    monitor: 'ICMP',
    nodeId: route.params.id as string,
    instance: instance.value,
    timeRange: 10,
    timeRangeUnit: TimeRangeUnit.Minute
  }
})

const bytesIn = computed<GraphProps>(() => {
  return {
    label: 'Bits Inbound',
    metrics: ['network_in_bits'],
    monitor: 'SNMP',
    nodeId: route.params.id as string,
    instance: instance.value,
    timeRange: 10,
    timeRangeUnit: TimeRangeUnit.Minute
  }
})

const bytesOut = computed<GraphProps>(() => {
  return {
    label: 'Bits Outbound',
    metrics: ['network_out_bits'],
    monitor: 'SNMP',
    nodeId: route.params.id as string,
    instance: instance.value,
    timeRange: 10,
    timeRangeUnit: TimeRangeUnit.Minute
  }
})

const bytesInOut = computed<GraphProps>(() => {
  return {
    label: 'Bits Inbound / Outbound',
    metrics: ['network_in_bits', 'network_out_bits'],
    monitor: 'SNMP',
    nodeId: route.params.id as string,
    instance: instance.value,
    timeRange: 10,
    timeRangeUnit: TimeRangeUnit.Minute
  }
})

const onDownload = () => {
  const page = document.getElementById('graphs-container') as HTMLElement
  const canvases = document.getElementsByClassName('canvas') as HTMLCollectionOf<HTMLCanvasElement>
  downloadMultipleCanvases(page, canvases)
}

onMounted(async () => {
  store.setNodeId(Number(route.params.id))
  await store.fetchNode()
})
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';

.header-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin: var(variables.$spacing-xl) var(variables.$spacing-l);

  :deep(.headline) {
    margin: 0;
  }
}

#graphs-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 2rem;
  margin: var(variables.$spacing-xl) var(variables.$spacing-l);
}
</style>
