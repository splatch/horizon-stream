import { makeExecutableSchema } from '@graphql-tools/schema'
import { device, listDevices } from './store/Queries/devices'
import typeDefs from './typeDefs'

const resolvers = {
  Query: {
    device: () => device,
    listDevices: () => listDevices
  },
  Mutation: {
    saveRoutingKey: (_: any, { key }) => key,
    saveDevice: (_: any, { device }) => device.name
  }
}

const schema = makeExecutableSchema({
  resolvers: [resolvers],
  typeDefs: [typeDefs]
})

export default {
  schema
}
