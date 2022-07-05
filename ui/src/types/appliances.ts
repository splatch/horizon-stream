/**
 * Minion
 */
export interface Minion {
  id: string
  status: string
  latency: string
  cpu_util: string
}

/**
 * Device
 */
export interface Device {
  id: string
  name: string
  icmp_latency: string
  snmp_uptime: string
}
export const defaultDevice = {
  id: '0',
  name: 'name0',
  icmp_latency: 'latency0',
  snmp_uptime: 'uptime0'
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