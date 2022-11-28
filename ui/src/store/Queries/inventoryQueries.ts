import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { NodesListDocument, Node } from '@/types/graphql'
import useSpinner from '@/composables/useSpinner'

export const useInventoryQueries = defineStore('inventoryQueries', () => {
  const nodes = ref({})
  
  const { startSpinner, stopSpinner } = useSpinner()

  const { data, isFetching, execute } = useQuery({
    query: NodesListDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })
  
  const formatNodesMetrics = (data: any) => {
    return data.nodes?.map((node: Node, i: number) => ({
      id: node.id,
      label: node.nodeLabel,
      // TODO mocked: should be replace with real metrics when avail.
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
    isFetching.value ? startSpinner() : stopSpinner()

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
