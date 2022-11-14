import { ExtendedDeviceDTO } from '@/types/device'
import { ExtendedMinionDTO } from '@/types/minion'
import { add, intervalToDuration, formatDuration } from 'date-fns'
import { TimeUnit } from '@/types'

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
 * Translate timestamp to human-readeable duration
 * @param timestamp seconds/milliseconds
 * @param timeUnit milliseconds by default
 * @returns A shorted version (e.g. 28d4h22m16s) 
 */
export const getHumanReadableDuration = (timestamp: number, timeUnit = TimeUnit.MSecs) => {
  let durationDisplay = '--' // undefined | null

  if(![undefined, null].includes(timestamp as any)) {
    const timestampInSecs = timeUnit === TimeUnit.Secs ? timestamp : timestamp / 1000
    if(Math.abs(timestampInSecs) < 1) {
      durationDisplay = String(timestamp)
      if(timestampInSecs !== 0) durationDisplay += 'ms'
    } else {
      const duration = intervalToDuration({
        start: new Date(),
        end: add(new Date(), { seconds: timestampInSecs })
      })
        
      let durationFormatted = formatDuration(duration, { format: ['days', 'hours', 'minutes', 'seconds']})
      const re = /(?<s>seconds?)|(?<m>\minutes?)|(?<h>hours?)|(?<d>days?)/gm
    
      // replace days/hours/minutes/seconds by d/h/m/s
      for(let mat of durationFormatted.matchAll(re)) {
        const { s, m, h, d } = mat.groups as Record<string, string>
        if(s) durationFormatted = durationFormatted.replace(s, 's')
        if(m) durationFormatted = durationFormatted.replace(m, 'm')
        if(h) durationFormatted = durationFormatted.replace(h, 'h')
        if(d) durationFormatted = durationFormatted.replace(d, 'd')
      }
    
      durationDisplay = durationFormatted.replaceAll(' ', '')
    }
  }

  return durationDisplay
}
