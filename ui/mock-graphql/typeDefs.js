const typeDefs = `
  type User {
    id: ID!
    name: String!
    password: String!
  },
  type Query {
    foo: User!
    bar: [User!]!
  },
  type Mutation {
    saveRoutingKey(key: String!): String
  }
`

export default typeDefs
