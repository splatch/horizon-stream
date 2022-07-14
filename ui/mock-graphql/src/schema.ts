import { makeExecutableSchema } from '@graphql-tools/schema'
import typeDefs from './typeDefs.js'
import { device, listDevices } from './store/Queries/devices.js'
import { minion, listMinions } from './store/Queries/minions.js'

const resolvers = {
  Query: {
    device: () => device,
    listDevices: () => listDevices,
    minion: () => minion,
    listMinions: () => listMinions
  },
  Mutation: {
    saveRoutingKey: (_: any, { key }: { key: string }) => key
  }
}

const schema = makeExecutableSchema({
  resolvers: [resolvers],
  typeDefs: [typeDefs]
})

export default {
  schema
}
