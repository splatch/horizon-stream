export interface Device {
  id: string
  name: string
  icmp_latency: string
  snmp_uptime: string
}

export interface Devices {
  items: Device[],
  count: null | number,
  totalCount: number,
  offset: number
}

export const DevicesQuery = `
  {
    listDevices {
      items {
        id
        name
        icmp_latency
        snmp_uptime
      }
      count
      totalCount
      offset
    }
  }
`
