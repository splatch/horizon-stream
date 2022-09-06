<template>
<h1>{{data.node?.label}} (id: {{data.node?.id}})</h1>

<pre>Event: {{data.event}}</pre>
<pre>Node: {{data.node}}</pre>
<pre>Latency: {{data.latency}}</pre>
<pre>Uptime: {{data.uptime}}</pre>
</template>

<script setup lang="ts">
import { useNodeStore } from '@/store/Views/nodeStore'

const nodeStore = useNodeStore()
const data = computed(() => {
  const route = useRoute()
  const fetchedData = nodeStore.fetchedData
  return {
    event: fetchedData.events?.filter((event: any) => event.nodeId == route.params.id)[0],
    node: fetchedData.devices?.filter((device: any) => device.id == route.params.id)[0],
    latency: fetchedData.deviceLatency,
    uptime: fetchedData.deviceUptime
  }
})
</script>

<style lang="scss">
</style>