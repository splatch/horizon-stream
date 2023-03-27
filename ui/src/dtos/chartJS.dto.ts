import { FlowsApplicationChartData, FlowsApplicationData, FlowsLineChartItem } from '@/types'

export const flowApptoChartJSLine = (data: FlowsApplicationData[]) => {
  const transformedData = data.reduce((result: any, item) => {
    const { label, ...data } = item
    const existingIndex = result.findIndex((r: any) => r.label === label)
    if (existingIndex === -1) {
      result.push({ label, data: [data as FlowsApplicationChartData] })
    } else {
      result[existingIndex].data.push(data)
    }
    return result
  }, [])
  return transformedData as FlowsLineChartItem[]
}
