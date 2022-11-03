import { ExtendedDeviceDTO } from '@/types/device'
import { ExtendedMinionDTO } from '@/types/minion'
import { add, intervalToDuration, formatDuration } from 'date-fns'

export interface BGColors {
  latencyBgColor: string
  uptimeBgColor: string
  statusBgColor: string
}

/**
 * Change color background on metrics value
 * @param list 
 * @returns list of items with added metrics background color props
 */
export const formatItemBgColor = (list: ExtendedMinionDTO[] | ExtendedDeviceDTO[]) => list.map(item => {
  const { icmp_latency: latency, snmp_uptime: uptime, status } = item
  const bg = {
    ok: 'ok',
    failed: 'failed',
    unknown: 'unknown' // undefined | null
  }

  const setBgColor = (metric: number) => {
    let bgColor = bg.unknown
    
    if(![undefined, null].includes(metric as any)) {
      bgColor = metric >= 0 ? bg.ok : bg.failed
    }

    return bgColor
  } 

  return {
    ...item,
    latencyBgColor: setBgColor(latency),
    uptimeBgColor: setBgColor(uptime),
    statusBgColor: status === 'UP' ? bg.ok : bg.failed
  }
})

/**
 * 
 * @param timestamp 
 * @param timeUnit 
 * @returns 
 */
export const getHumanReadableDuration = (timestamp: number, timeUnit = 'secs') => {
  let durationDisplay = '--' // undefined | null

  if(![undefined, null].includes(timestamp as any)) {
    const duration = intervalToDuration({
      start: new Date(),
      end: add(new Date(), {seconds: timeUnit === 'secs' ? timestamp : timestamp / 1000})
    })
      
    durationDisplay = formatDuration(duration, { format: ['days', 'hours', 'minutes']})
  }

  return durationDisplay
}

export const formatLatencyDisplay = (latency: any) => {
  let display = '--' // undefined | null

  if(![undefined, null].includes(latency)) {
    display = latency

    if(latency !== 0) display += 'ms'
  }

  return display
}