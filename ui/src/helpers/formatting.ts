import { ExtendedDeviceDTO } from "@/types/device"
import { ExtendedMinionDTO } from "@/types/minion"

export interface BGColors {
  statusBgColor: string
  latencyBgColor: string
  uptimeBgColor: string
}

/**
 * 
 * @param list 
 * @returns 
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
