const typeDefs = `
  type Device {
    id: ID!
    name: String!
    icmp_latency: String!
    snmp_uptime: String!
  }
  type ListDevices {
    devices: [Device!]!
    count: String!
    totalCount: String!
    offset: String!
  },
  type Query {
    device: Device!
    listDevices: ListDevices!
  },
  type Mutation {
    saveRoutingKey(key: String!): String
  }
`

export default typeDefs
