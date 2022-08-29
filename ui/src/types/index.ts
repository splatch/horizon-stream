import { SORT } from '@featherds/table'

export interface SnackbarProps {
  msg: string
  center?: boolean
  error?: boolean
}

export interface IdLabelProps {
  id: string
  label: string
}

export interface FeatherSortObject {
  property: string
  value: SORT | any
}