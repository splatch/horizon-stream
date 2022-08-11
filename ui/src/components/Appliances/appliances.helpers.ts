/**
 * Change color background on metrics value
 * @param list 
 * @returns list of items with added metrics background color props
 */
export const formatItemBgColor = (list: any[]): Record<string, string>[] => list.map(item => {
  const bg = {
    ok: 'bg-ok',
    failed: 'bg-failed',
    unknown: 'bg-unknown'
  }
  
  let statusBgColor = bg.ok
  if(item?.status === 'DOWN') statusBgColor = bg.failed
  else if(item?.status === 'UNKNOWN') statusBgColor = bg.unknown

  let latencyBgColor = bg.ok
  if(item?.icmp_latency === 'FAILED') latencyBgColor = bg.failed
  else if(item?.icmp_latency === 'UNKNOWN') latencyBgColor = bg.unknown

  let uptimeBgColor = bg.ok
  if(item?.snmp_uptime === 'FAILED') uptimeBgColor = bg.failed
  else if(item?.snmp_uptime === 'UNKNOWN') uptimeBgColor = bg.unknown

  return {
    ...item,
    statusBgColor,
    latencyBgColor,
    uptimeBgColor
  }
})
