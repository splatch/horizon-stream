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
  const bg = {
    ok: 'bg-ok',
    failed: 'bg-failed',
    unknown: 'bg-unknown'
  }

  let statusBgColor = bg.ok
  if(item?.status === 'DOWN') statusBgColor = bg.failed
  else if(item?.status === 'UNKNOWN') statusBgColor = bg.unknown

  let latencyBgColor = bg.ok
  if(item?.icmp_latency > 100) latencyBgColor = bg.failed
  else if(item?.icmp_latency === null) latencyBgColor = bg.unknown

  let uptimeBgColor = bg.ok
  if(item?.snmp_uptime === 0) uptimeBgColor = bg.failed
  else if(item?.snmp_uptime === null) uptimeBgColor = bg.unknown

  return {
    ...item,
    statusBgColor,
    latencyBgColor,
    uptimeBgColor
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
