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
    const { data, isFetching } = await useQuery({
      query: NodeLatencyMetricDocument,
      variables: { id: nodeId },
      cachePolicy: 'network-only' // always fetch and do not cache
    })

    watchEffect(() => {
      if(data.value && !isFetching.value) {
        const res = data.value?.nodeLatency?.data?.result[0]
        metricsNodes.value.push({
          id: res.metric.node_id,
          latency: res.value[1],
          uptime: res.value[0],
          status: ''
        })
      }
    })
  }

  const formatNodesData = () => {
    const getMetric = (nodeId => metricsNodes.value.filter(node => Number(node.id) === nodeId))

    watch(metricsNodes.value, () => {
      const nodeDetail = allNodes.value.map(node => {
        const metric = getMetric(node.id)
        return {
          id: node.id,
          label: node.nodeLabel,
          status: '',
          metrics: [
            {
              type: 'latency',
              label: 'Latency',
              timestamp: metric.latency || '--',
              status: ''
            },
            {
              type: 'uptime',
              label: 'Uptime',
              timestamp: metric.uptime || '--',
              timeUnit: TimeUnit.MSecs,
              status: ''
            },
            {
              type: 'status',
              label: 'Status',
              status: metric.status || '--'
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
        }
      })

      nodes.value = nodeDetail
    }, {deep: true})
  }

  watchEffect(async () => {
    nodesFetching.value ? startSpinner() : stopSpinner()

    if(nodesData.value && nodesData.value?.findAllNodes?.length) {
      allNodes.value = nodesData.value.findAllNodes
      
      allNodes.value.forEach(node => fetchNodeMetrics(node.id))
      
      formatNodesData()
    }
  })

  return { 
    nodes,
    fetch: execute
  }
})
