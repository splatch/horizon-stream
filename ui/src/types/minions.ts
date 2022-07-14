export interface Minion {
  id: string
  status: string
  latency: string
  cpu_util: string
}

export interface Minions {
  items: Minion[],
  count: null | number,
  totalCount: number,
  offset: number
}

export const MinionsQuery = `
  {
    listMinions {
      items {
        id
        status
        latency
        cpu_util
      }
      count
      totalCount
      offset
    }
  }
`
