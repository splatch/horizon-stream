const typeDefs = `
  type Device {
    id: ID!
    name: String!
    icmp_latency: String!
    snmp_uptime: String!
  }
  input DeviceInput {
    name: String
    icmp_latency: String
    snmp_uptime: String
    management_ip: String
    community_string: String
    port: Int
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
  type Mutation {
    saveDevice(device: DeviceInput!): String
  }
`

export default typeDefs
