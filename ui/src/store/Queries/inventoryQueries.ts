import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { NodesListDocument, NodeLatencyMetricDocument } from '@/types/graphql'
import { NodeContent } from '@/types/inventory'
import useSpinner from '@/composables/useSpinner'
import { TimeUnit } from '@/types'

interface NodeCard {
  id: number,
  label: string,
  latency: number,
  location: string,
  managementIp: string
}

export const useInventoryQueries = defineStore('inventoryQueries', () => {
  const nodes = ref([])

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
      // console.log('nodesData',nodesData.value)
      const { latencyData, latencyFetching } = getLatency()

      watch([latencyData, latencyFetching], (latency) => {
        const [ data, isFetching ] = latency

        isFetching ? startSpinner() : stopSpinner()
        
        if(data && !isFetching) {
          const node = nodesData.value?.findAllNodes[0]
          const latencyRes = latency[0].nodeLatency?.data.result[0]
          const nodeFormatted: NodeContent = {
            id: node?.id,
            label: node?.nodeLabel,
            metrics: [
              {
                type: 'latency',
                label: 'Latency',
                timestamp: latencyRes?.value[1],
                timeUnit: TimeUnit.MSecs,
                status: 'UP'
              }
            ],
            anchor: {
              locationValue: latencyRes?.metric.location || '--',
              managementIpValue: latencyRes?.metric.instance || '--'
            }
          }
          
          nodes.value = [nodeFormatted]
        }
      })
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
