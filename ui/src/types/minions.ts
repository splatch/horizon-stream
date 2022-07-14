export interface Minion {
  id: string
  label: string
  status: string
  location: string
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
        label
        status
        location
      }
      count
      totalCount
      offset
    }
  }
`
