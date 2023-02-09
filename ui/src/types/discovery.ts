export enum DiscoverytType {
  None,
  ICSNMP,
  Azure,
  SysLog,
  SNMPTraps
}

export interface IDiscoverySNMPInput {
  type: DiscoverytType
  name: string
  location: string
  IPRange: string
  communityString: string
  UDPPort: number
}
