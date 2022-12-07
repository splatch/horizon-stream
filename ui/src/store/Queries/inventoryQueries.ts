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
  const allNodes = ref([])
  const metricsNodes = ref([])
  
  const { startSpinner, stopSpinner } = useSpinner()

  const { data: nodesData, isFetching: nodesFetching, execute } = useQuery({
    query: NodesListDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  const fetchNodeMetrics = async (nodeId: number) => {
    return await useQuery({
      query: NodeLatencyMetricDocument,
      variables: { id: nodeId },
      cachePolicy: 'network-only' // always fetch and do not cache
    })
  }

  watchEffect(async () => {
    nodesFetching.value ? startSpinner() : stopSpinner()

    if(nodesData.value && nodesData.value?.findAllNodes?.length) {
      allNodes.value = nodesData.value.findAllNodes

      allNodes.value.forEach(async (node) => {
        const {data, isFetching} = await fetchNodeMetrics(node.id)

        if(data && !isFetching.value) {
          const res = data.value.nodeLatency.data.result[0]
        
          metricsNodes.value.push({
            id: node.id,
            label: node.nodeLabel,
            status: '',
            metrics: [
              {
                type: 'latency',
                label: 'Latency',
                timestamp: res.value[1] || '--',
                status: ''
              },
              {
                type: 'uptime',
                label: 'Uptime',
                timestamp: res.value[0] || '--',
                timeUnit: TimeUnit.MSecs,
                status: ''
              },
              {
                type: 'status',
                label: 'Status',
                status: res.metric.status || '--'
              }
            ],
            anchor: {
              profileValue: '--',
              profileLink: '',
              locationValue: node.location.location || '--',
              locationLink: '',
              ipAddressValue: node.ipInterfaces[0].ipAddress || '',
              ipAddressLink: '',
              tagValue: '--',
              tagLink: ''
            }  
          })
        }
      })

      nodes.value = metricsNodes.value
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
