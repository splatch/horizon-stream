export interface Minion {
  id: string
  status: string
  latency: string
  cpu_util: string
}

export interface Device {
  id: string
  name: string
  icmp_latency: string
  snmp_uptime: string
}
