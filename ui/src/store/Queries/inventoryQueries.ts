import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { NodesListDocument } from '@/types/graphql'

export const useInventoryQueries = defineStore('inventoryQueries', () => {
  const nodes = ref({})
  
  const { data, execute } = useQuery({
    query: NodesListDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  // const formatNodesMetrics = (data: {nodes, latency, uptime}) => {
  const formatNodesMetrics = (data) => {
    return data.nodes?.map((node, i) => ({
      id: node.id,
      label: node.nodeLabel,
      metrics: [
        {
          type: 'latency',
          label: 'Latency',
          timestamp: data.latency[i],
          timeUnit: null,
          status: 'DOWN'
        },
        {
          type: 'uptime',
          label: 'Uptime',
          timestamp: data.uptime[i],
          timeUnit: null,
          status: 'UNKNOWN'
        },
        {
          type: 'status',
          label: 'Status',
          status: 'UP'
        }
      ]
    }))
  }

  watchEffect(() => {
    const nodesData = {
      nodes: data.value?.findAllNodes,
      latency: data.value?.nodesLatency,
      uptime: data.value?.nodesUptime
    }
    nodes.value = formatNodesMetrics(nodesData)
  })

  return {
    nodes,
    fetch: execute
  }
})
