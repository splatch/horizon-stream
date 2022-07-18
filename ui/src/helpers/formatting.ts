export const formatItemBgColor = (list: [any]) => list.map(item => {
  let statusClass = 'bg-success'
  if(item?.status === 'down') statusClass = 'bg-error'

  let latencyClass = 'bg-success'
  if(Number(item?.icmp_latency) >= 100) latencyClass = 'bg-error'
  else if(Number(item?.icmp_latency) >= 60) latencyClass = 'bg-warning'

  let uptimeClass = 'bg-success'
  if(Number(item?.snmp_uptime) >= 168) uptimeClass = 'bg-error'
  else if(Number(item?.snmp_uptime) >= 48) uptimeClass = 'bg-warning'

  return {
    ...item,
    statusClass,
    latencyClass,
    uptimeClass
  }
})
