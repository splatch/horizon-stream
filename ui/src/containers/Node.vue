<template>
<h1>{{headerLabel}} (id: {{route.params.id}})</h1>
<EventsWidget :data="nodeData" />
</template>

<script setup lang="ts">
import { useNodeStore } from '@/store/Views/nodeStore'
import EventsWidget from '@/components/Widgets/EventsWidget.vue'

const nodeStore = useNodeStore()
const route = useRoute()

const headerLabel = computed(() => nodeStore.fetchedData?.device?.label)
const nodeData = computed(() => ({
  events: nodeStore.fetchedData?.events?.filter((event: any) => event.nodeId == route.params.id),
  node: nodeStore.fetchedData?.device,
  latencies: nodeStore.fetchedData?.deviceLatency,
  uptimes: nodeStore.fetchedData?.deviceUptime
}))

nodeStore.setNodeId(Number(route.params.id))
</script>

<style lang="scss">
</style>