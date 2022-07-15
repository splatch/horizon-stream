import { makeExecutableSchema } from '@graphql-tools/schema'
import typeDefs from './typeDefs.js'
import { device, listDevices } from './store/Queries/devices.js'

const resolvers = {
  Query: {
    device: () => device,
    listDevices: () => listDevices
  },
  Mutation: {
    saveRoutingKey: (_, { key }) => key
  }
}

const schema = makeExecutableSchema({
  resolvers: [resolvers],
  typeDefs: [typeDefs]
})

export default {
  schema
}
