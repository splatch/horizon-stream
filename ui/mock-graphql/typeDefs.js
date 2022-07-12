const typeDefs = `
  type User {
    id: ID!
    name: String!
    password: String!
  },
  type Query {
    foo: User!
    bar: [User!]!
  }
`

export default typeDefs
