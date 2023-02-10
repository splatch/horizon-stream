export interface IDiscoverySNMPInput {
  type: DiscoverytType
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
