export interface Device {
  id: string
  name: string
  icmp_latency: string
  snmp_uptime: string
}

export interface Devices {
  list: Device[],
  count: null | number,
  totalCount: number,
  offset: number
}

export const defaultDevice: Devices = {
  list: [],
  count: null,
  totalCount: 0,
  offset: 0
}

export interface State {
  deviceList: Device[]
}

export const defaultState: State = {
  deviceList: []
}