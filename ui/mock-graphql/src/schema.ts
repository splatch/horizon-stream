import { makeExecutableSchema } from '@graphql-tools/schema'
import typeDefs from './typeDefs'
import { device, listDevices } from './data/device'
import { minion, listMinions } from './data/minions'

const resolvers = {
  Query: {
    device: () => device,
    listDevices: () => listDevices,
    minion: () => minion,
    listMinions: () => listMinions
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
