import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { 
  NodesListDocument,
  NodeLatencyMetricDocument,
  Node,
  TsResult
} from '@/types/graphql'
import { NodeContent } from '@/types/inventory'
import useSpinner from '@/composables/useSpinner'
import { TimeUnit } from '@/types'

export const useInventoryQueries = defineStore('inventoryQueries', () => {
  const nodes = ref<NodeContent[]>([])

  const { startSpinner, stopSpinner } = useSpinner()

  const { data: nodesData, isFetching: nodesFetching, execute } = useQuery({
    query: NodesListDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  const getLatency = () => {
    const { data: latencyData, isFetching: latencyFetching } = useQuery({
      query: NodeLatencyMetricDocument,
      cachePolicy: 'network-only' // always fetch and do not cache
    })

    return {
      latencyData,
      latencyFetching
    }
  }

  watchEffect(() => {
    nodesFetching.value ? startSpinner() : stopSpinner()

    if(nodesData.value) {
      const { latencyData, latencyFetching } = getLatency()

      watch([latencyData, latencyFetching], (latency) => {
        const [ data, isFetching ] = latency

        isFetching ? startSpinner() : stopSpinner()
        
        if(data && !isFetching) {
          const node = nodesData.value?.findAllNodes as Node[]
          
          const latencyRes = data.nodeLatency?.data?.result as TsResult[]
          const resValue = latencyRes[0].value as number[]
          const resMetric = latencyRes[0].metric as Record<string, string>

          const nodeFormatted: NodeContent = {
            id: node[0].id,
            label: node[0].nodeLabel,
            metrics: [
              {
                type: 'latency',
                label: 'Latency',
                timestamp: resValue[1],
                timeUnit: TimeUnit.MSecs,
                status: 'UP'
              }
            ],
            anchor: {
              locationValue: resMetric.location || '--',
              managementIpValue: resMetric.instance || '--'
            }
          }

          nodes.value = [ nodeFormatted ]
        }
      })
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
