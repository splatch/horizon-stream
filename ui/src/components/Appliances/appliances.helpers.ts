import { ExtendedDeviceDTO } from '@/types/device'
import { ExtendedMinionDTO } from '@/types/minion'
import { add, intervalToDuration, formatDuration } from 'date-fns'

export interface BGColors {
  statusBgColor: string
  latencyBgColor: string
  uptimeBgColor: string
}

/**
 * Change color background on metrics value
 * @param list 
 * @returns list of items with added metrics background color props
 */
export const formatItemBgColor = (list: ExtendedMinionDTO[] | ExtendedDeviceDTO[]) => list.map(item => {
  const {icmp_latency: latency, snmp_uptime: uptime, status} = item
  const bg = {
    ok: 'bg-ok',
    failed: 'bg-failed',
    unknown: 'bg-unknown'
  }

  // in ms
  let latencyBgColor = bg.unknown
  if(latency >= 0) latencyBgColor = latency > 100 ? bg.failed : bg.ok

  let uptimeBgColor = bg.unknown
  if(uptime >= 0) uptimeBgColor = uptime === 0 ? bg.failed : bg.ok

  const statusBgColor = status === 'DOWN' ? bg.failed : bg.ok

  return {
    ...item,
    latencyBgColor,
    uptimeBgColor,
    statusBgColor
  }
})

export const getHumanReadableDuration = (uptimeInSeconds: number) => {
  if (uptimeInSeconds < 60) return `${uptimeInSeconds} ${uptimeInSeconds <= 0 ? '' : 'seconds'}`

  const duration = intervalToDuration({
    start: new Date(),
    end: add(new Date(), {seconds: uptimeInSeconds})
  })

  return formatDuration(duration, { format: ['days', 'hours', 'minutes']})
}
