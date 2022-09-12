<template>
<h1>{{headerLabel}} (id: {{route.params.id}})</h1>
<EventsWidget :data="data" />
</template>

<script setup lang="ts">
import { useNodeStore } from '@/store/Views/nodeStore'
import EventsWidget from '@/components/Widgets/EventsWidget.vue'

const nodeStore = useNodeStore()
const route = useRoute()

const headerLabel = ref()

const data = computed(() => {
  const fetchedData = nodeStore.fetchedEvents
  const nodeData = fetchedData.devices?.filter((device: any) => device.id == route.params.id)[0]
  
  headerLabel.value = nodeData?.label

  return {
    events: fetchedData.events?.filter((event: any) => event.nodeId == route.params.id),
    node: nodeData,
    latencies: fetchedData.deviceLatency,
    uptimes: fetchedData.deviceUptime
  }
})
</script>

<style lang="scss">
</style>