export interface DiscoveryInput {
  type: number
  name: string
  location: string
  IPRange: string
  communityString: string
  UDPPort: number
}

export interface IDiscovery {
  id: number
  name: string
}
