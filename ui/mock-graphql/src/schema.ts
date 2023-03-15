import { makeExecutableSchema } from '@graphql-tools/schema'
import typeDefs from './typeDefs'
import { listAzureDiscoveries } from './data/discovery'
import { alertList } from './data/alerts'

const resolvers = {
  Query: {
    listAzureDiscoveries: () => listAzureDiscoveries,
    alertList: () => alertList
  }
}

const schema = makeExecutableSchema({
  resolvers: [resolvers],
  typeDefs: [typeDefs]
})

export default {
  schema
}
