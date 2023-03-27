import { FlowsApplicationChartData, FlowsApplicationData, FlowsLineChartItem } from '@/types'

export const flowApptoChartJSLine = (data: FlowsApplicationData[]) => {
  const appNames = [...new Set(data.map((obj) => obj.label))]
  let objects = appNames.reduce(
    (a, v) => ({
      ...a,
      [v]: {}
    }),
    {}
  )
  Object.entries(objects).forEach((app) => {
    let objArr = []
    objArr = data
      .filter((item) => item.label === app[0])
      .map((item) => ({
        timestamp: item.timestamp,
        value: item.value,
        direction: item.direction
      }))

    objects = { ...objects, [app[0]]: { ...objArr } }
  })

  return Object.keys(objects).map((key: any) => {
    const nested = objects[key as keyof typeof objects]
    return {
      label: key,
      data: Object.keys(nested).map((dataKey) => ({ ...(nested[dataKey] as FlowsApplicationChartData) }))
    } as FlowsLineChartItem
  })
}
