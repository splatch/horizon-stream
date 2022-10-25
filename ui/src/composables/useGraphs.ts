import { useQuery } from 'villus'
import { GetMetricDocument } from '@/types/graphql'
import { DataSets, GraphMetric } from '@/types/graphs'
import { getMockData } from '@/types/mocks'

export const useGraphs = () => {
  const variables = ref({ metric: ''})
  // const variables = ref({} as GraphMetric)
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
  // TODO: once BE avail, use GraphMetric might be needed
  /* const getMetrics = async (graph: GraphMetric) => {
    for (const graphMetric of graph.metrics) {
      variables.value = { 
        metric: {
          name: graph.label,
          labels: {
            location: graph.location, 
            instance: graph.id
          }
        }
      }
      
      await getMetric()
      
      const {metric, values} = data.value?.metric?.data?.result?.[0]
      
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
  } */

  return {
    getMetrics,
    dataSets: computed<DataSets>(() => Object.values(dataSetsObject))
  }
}
