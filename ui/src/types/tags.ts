export enum TagNodesType {
  Unselected = 0,
  All = 1,
  Individual = 2,
  Clear = 3
}

export interface Tag {
  id: number
  label: string
}
