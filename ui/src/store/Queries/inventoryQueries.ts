import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { 
  NodesListDocument,
  NodeLatencyMetricDocument,
  Node,
  TsResult,
  IpInterface,
  Location
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
        const [ metric, metricFetching ] = latency

        metricFetching ? startSpinner() : stopSpinner()
        
        if(metric && !metricFetching) {
          const allNodes = nodesData.value?.findAllNodes as Node[]
          const nodeIpInterfaces = allNodes[0].ipInterfaces as IpInterface[]
          const nodeLocation = allNodes[0].location as Location
          
          const latencyRes = metric.nodeLatency?.data?.result as TsResult[]
          console.log('latencyRes',latencyRes)
          
          if(latencyRes.length) {
            const latencyResValue = latencyRes[0].value as number[]
  
            const nodeFormatted: NodeContent = {
              id: allNodes[0].id,
              label: allNodes[0].nodeLabel,
              metrics: [
                {
                  type: 'latency',
                  label: 'Latency',
                  timestamp: latencyResValue[1],
                  timeUnit: TimeUnit.MSecs,
                  status: 'UP'
                }
              ],
              anchor: {
                locationValue: nodeLocation.location || '--',
                managementIpValue: nodeIpInterfaces[0].ipAddress || '--'
              }
            }
  
            nodes.value = [ nodeFormatted ]
          }
        }
      })
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
