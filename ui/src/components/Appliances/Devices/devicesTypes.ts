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

export const defaultDevices: Devices = {
  items: [],
  count: null,
  totalCount: 0,
  offset: 0
}

export interface State {
  deviceItems: Device[]
}

export const defaultState: State = {
  deviceItems: []
}