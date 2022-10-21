import { useQuery } from 'villus'
import { GetMetricDocument } from '@/types/graphql'
import { DataSets } from '@/types/graphs'
export const useGraphs = () => {
  const variables = ref({ metric: '' })
  const dataSetsObject = reactive({} as any)

  // TODO: to remove once real data avail
  const mockData = (data: any) => {
    const values = [
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
    return {
      ...data,
      values
    }
  }
  
  const { data, execute: getMetric } = useQuery({
    query: GetMetricDocument,
    cachePolicy: 'network-only',
    fetchOnMount: false,
    variables
  })

  const getMetrics = async (metricsStrs: string[]) => {
    for (const metricStr of metricsStrs) {
      variables.value = { metric: metricStr }
      
      await getMetric()
      
      const {metric, values, value} = mockData(data.value?.metric?.data?.result?.[0])

      if(values.length) {
        dataSetsObject[metric.__name__] = {
          metric,
          values: values.filter(val => {
            const [timestamp, value] = val
            if(timestamp && value) return val
          })
        }
      }
    }
  }

  return {
    getMetrics,
    dataSets: computed<DataSets>(() => Object.values(dataSetsObject))
  }
}
