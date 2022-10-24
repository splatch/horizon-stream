import { useQuery } from 'villus'
import { GetMetricDocument } from '@/types/graphql'
import { DataSets } from '@/types/graphs'
import { getMockData } from '@/types/mocks'

export const useGraphs = () => {
  const variables = ref({ metric: '' })
  const dataSetsObject = reactive({} as any)

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
      
      // const {metric, values} = data.value?.metric?.data?.result?.[0]
      const {metric, values} = getMockData(metricStr) // TODO: to be removed once real data avail

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
