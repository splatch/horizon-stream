const typeDefs = `
  type Device {
    id: String!
    name: String!
    status: String!
    icmp_latency: String!
    snmp_uptime: String!
  }
  type ListDevices {
    items: [Device!]!
    count: String!
    totalCount: String!
    offset: String!
  },
  type Minion {
    id: String!
    label: String!
    status: String!
    location: String!
    lastUpdated: String!
    icmp_latency: String!
    snmp_uptime: String!
  }
  type ListMinions {
    minions: [Minion!]!
  },
  type Query {
    device: Device!
    listDevices: ListDevices!
    minion: Minion!
    listMinions: ListMinions!
  },
  type Mutation {
    saveRoutingKey(key: String!): String
  }
`

export default typeDefs
