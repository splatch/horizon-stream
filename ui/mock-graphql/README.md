This project serves as a GraphQL mock server.

- It can run a local server (nodemon), with mocked queries if needed.
- It can automate and generate typing of GraphQL operations (codegen) from .graphql files (querying purposes)

## Installation

```
yarn install
```

## Mock server

```
yarn serve
```

Serves at: `http://127.0.0.1:4000/graphql`

## Typing generation

```
yarn generate
```

Note: required mock server be running

## Config

.env.development.local

```
VITE_BASE_URL=http://127.0.0.1:4000/api # GraphQL mock server (./mock-graphql)
```

## Usage example

```
import { useQuery } from 'villus'
import { ListAzureDiscoveriesDocument } from '@/types/graphql-mocks'

const { data, execute } = useQuery({
  query: ListAzureDiscoveriesDocument
})
```
