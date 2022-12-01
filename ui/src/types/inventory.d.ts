import { Chip } from './metric'

interface Anchor {
  profileValue?: number,
  profileLink?: string,
  locationValue?: string,
  locationLink?: string,
  ipAddressValue?: number,
  ipAddressLink?: string,
  tagValue?: number,
  tagLink?: string,
}

interface NodeContent {
  id: number,
  label: string | undefined,
  metrics: Chip[],
  anchor: Anchor
}
