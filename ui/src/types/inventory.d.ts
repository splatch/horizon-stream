import { Chip } from './metric'

interface Anchor {
  profileValue: number,
  profileLink: string,
  locationValue: string,
  locationLink: string,
  ipInterfaceValue: number,
  ipInterfaceLink: string,
  tagValue: number,
  tagLink: string,
}

interface NodeContent {
  id: number,
  label: string,
  metrics: Chip[],
  anchor: Anchor
}
