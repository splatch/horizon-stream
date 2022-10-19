import { useQuery } from 'villus'
import { GetMetricDocument } from '@/types/graphql'
import { DataSets } from '@/types/graphs'
// import { ExtendedTsResult } from '@/types/metric'
export const useGraphs = () => {
  const variables = ref({ metric: '' })
  const dataSetsObject = reactive({} as any)

  // TODO: to remove once real data avail
  const mockData = (data: any) => {
    return {
      ...data,
      values: [
        [
          1665763347.046,
          '10.069208'
        ],
        [
          1665763362.061,
          '8.355875'
        ],
        [
          1665763377.047,
          '6.045458'
        ],
        [
          1665763392.050,
          '31.727167'
        ]
      ]
    }
  }
  
  const getMetrics = async (metrics: string[]) => {
    for (const metric of metrics) {
      variables.value = { metric }
      await getMetric()
      if (data.value) {
        // console.log('data.value',data.value)
        // dataSetsObject[data.value.metric?.data?.result?.[0].metric.__name__] = data.value.metric?.data?.result
        dataSetsObject[data.value.metric?.data?.result?.[0].metric.__name__] = mockData(data.value.metric?.data?.result?.[0])
      }
    }
  }

  const { data, execute: getMetric } = useQuery({
    query: GetMetricDocument,
    cachePolicy: 'network-only',
    fetchOnMount: false,
    variables
  })

  return {
    getMetrics,
    dataSets: computed<DataSets>(() => Object.values(dataSetsObject))
  }
}
