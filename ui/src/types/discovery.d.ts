export enum ContentEditableType {
  IP,
  community,
  port
}

export enum DiscoverytType {
  None,
  ICSNMP,
  Azure,
  SysLog,
  SNMPTraps
}

export interface DiscoveryInput {
  type: DiscoverytType
  name: string
  location: string
  IPRange: string
  communityString: string
  UDPPort: number
}
