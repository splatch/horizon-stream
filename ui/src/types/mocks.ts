import { EventDtoInput } from '@/types/graphql'
import { add } from 'date-fns'

const mockEvent: EventDtoInput = {
  uei: 'uei.opennms.org/alarms/trigger',
  source: 'kanata-office',
  time: add(new Date(), { days: 1 }).toISOString(),
  description: 'A problem has been triggered...'
}

export const getMockEvent = (): EventDtoInput => mockEvent


// TODO: to be removed once real data avail
export const getMockData = (metricStr: string) => {
  const randomValue = () => {
    const [min, max, decimal] = metricStr === 'snmp_round_trip_time_msec' ? [3000, 3600, 0] : [2, 15, 5]
    return (Math.random() * (max - min) + min).toFixed(decimal)
  }

  const uptimeValues = [[1666379625.633,randomValue()], [1666379640.647,randomValue()], [1666379655.647,randomValue()], [1666379670.649,randomValue()], [1666379685.649,randomValue()], [1666379700.650,randomValue()], [1666379715.649,randomValue()], [1666379730.650,randomValue()], [1666379745.650,randomValue()], [1666379760.650,randomValue()], [1666379775.651,randomValue()], [1666379790.650,randomValue()], [1666379805.650,randomValue()], [1666379820.651,randomValue()], [1666379835.651,randomValue()], [1666379850.651,randomValue()], [1666379865.651,randomValue()], [1666379880.651,randomValue()], [1666379895.652,randomValue()], [1666379910.652,randomValue()], [1666379925.652,randomValue()], [1666379940.664,randomValue()], [1666379955.665,randomValue()], [1666379970.653,randomValue()], [1666379985.653,randomValue()], [1666380000.653,randomValue()], [1666380015.652,randomValue()], [1666380030.653,randomValue()], [1666380045.653,randomValue()], [1666380060.654,randomValue()], [1666380075.654,randomValue()], [1666380090.654,randomValue()], [1666380105.653,randomValue()], [1666380120.654,randomValue()], [1666380135.654,randomValue()], [1666380150.655,randomValue()], [1666380165.655,randomValue()], [1666380180.655,randomValue()], [1666380195.655,randomValue()], [1666380210.656,randomValue()]]
  
  const latencyValues = [[1666379565.632,randomValue()], [1666379580.633,randomValue()], [1666379595.633,randomValue()], [1666379610.633,randomValue()], [1666379625.633,randomValue()], [1666379640.647,randomValue()], [1666379655.647,randomValue()], [1666379670.649,randomValue()], [1666379685.649,randomValue()], [1666379700.650,randomValue()], [1666379715.649,randomValue()], [1666379730.650,randomValue()], [1666379745.650,randomValue()], [1666379760.650,randomValue()], [1666379775.651,randomValue()], [1666379790.650,randomValue()], [1666379805.650,randomValue()], [1666379820.651,randomValue()], [1666379835.651,randomValue()], [1666379850.651,randomValue()], [1666379865.651,randomValue()], [1666379880.651,randomValue()], [1666379895.652,randomValue()], [1666379910.652,randomValue()], [1666379925.652,randomValue()], [1666379940.664,randomValue()], [1666379955.665,randomValue()], [1666379970.653,randomValue()], [1666379985.653,randomValue()], [1666380000.653,randomValue()], [1666380015.652,randomValue()], [1666380030.653,randomValue()], [1666380045.653,randomValue()], [1666380060.654,randomValue()], [1666380075.654,randomValue()], [1666380090.654,randomValue()], [1666380105.653,randomValue()], [1666380120.654,randomValue()], [1666380135.654,randomValue()], [1666380150.655,randomValue()]]

  return {
    metric: {
      __name__: metricStr
    },
    values: metricStr === 'snmp_round_trip_time_msec' ? uptimeValues : latencyValues
  }
}