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

  const getLatency = (nodeId: number) => {
    const { data: latencyData, isFetching: latencyFetching } = useQuery({
      query: NodeLatencyMetricDocument,
      variables: { id: nodeId },
      cachePolicy: 'network-only' // always fetch and do not cache
    })

    return {
      latencyData,
      latencyFetching
    }
  }

  watchEffect(() => {
    nodesFetching.value ? startSpinner() : stopSpinner()

    if(nodesData.value && nodesData.value?.findAllNodes?.length) {
      const { latencyData, latencyFetching } = getLatency(1)

      watch([latencyData, latencyFetching], ([data, isFetching]) => {
        isFetching ? startSpinner() : stopSpinner()
        
        if(data && !isFetching) {
          const allNodes = nodesData.value?.findAllNodes as Node[]
          const nodeIpInterfaces = allNodes[0].ipInterfaces as IpInterface[]
          const nodeLocation = allNodes[0].location as Location
          
          const latencyRes = data.nodeLatency?.data?.result as TsResult[]
          
          const latencyResValue = latencyRes[0]?.value || []
  
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
      })
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
