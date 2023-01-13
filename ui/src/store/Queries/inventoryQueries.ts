import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { NodesListDocument, NodeLatencyMetricDocument, TsResult, IpInterface, Location } from '@/types/graphql'
import { NodeContent } from '@/types/inventory'
import useSpinner from '@/composables/useSpinner'
import { Monitor } from '@/types'

export const useInventoryQueries = defineStore('inventoryQueries', () => {
  const nodes = ref<NodeContent[]>([])

  const { startSpinner, stopSpinner } = useSpinner()

  const {
    data: nodesData,
    isFetching: nodesFetching,
    execute
  } = useQuery({
    query: NodesListDocument,
    cachePolicy: 'network-only' // always fetch and do not cache
  })

  const fetchNodeMetrics = (nodeId: number, instance: string) =>
    useQuery({
      query: NodeLatencyMetricDocument,
      variables: { id: nodeId, instance, monitor: Monitor.ICMP },
      cachePolicy: 'network-only' // always fetch and do not cache
    })

  watch(nodesFetching, (_, fetched) => (fetched ? stopSpinner() : startSpinner()))

  watchEffect(() => {
    nodes.value = []

    const allNodes = nodesData.value?.findAllNodes

    if (allNodes?.length) {
      allNodes.forEach(async ({ id, nodeLabel, location, ipInterfaces }) => {
        const { data, isFetching } = await fetchNodeMetrics(id, ipInterfaces?.[0].ipAddress as string) // currently only 1 interface per node

        if (data.value && !isFetching.value) {
          const [res] = data.value.nodeLatency?.data?.result as TsResult[]
          const status = data.value.nodeStatus?.status
          const [, latency] = res?.value || ([] as number[] | string[] | undefined[])
          const { location: nodeLocation } = location as Location
          const [{ ipAddress }] = ipInterfaces as IpInterface[]

          nodes.value.push({
            id: id,
            label: nodeLabel,
            status: '',
            metrics: [
              {
                type: 'latency',
                label: 'Latency',
                timestamp: latency,
                status: ''
              },
              {
                type: 'status',
                label: 'Status',
                status: status || ''
              }
            ],
            anchor: {
              profileValue: '--',
              profileLink: '',
              locationValue: nodeLocation || '--',
              locationLink: '',
              managementIpValue: ipAddress || '',
              managementIpLink: '',
              tagValue: '--',
              tagLink: ''
            }
          })
        }
      })
    }
  })

  return {
    nodes,
    fetch: execute
  }
})
