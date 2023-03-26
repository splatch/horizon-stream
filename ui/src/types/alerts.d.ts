import { Alert } from './graphql'
import { TimeType } from '@/components/Alerts/alerts.constant'

interface IAlert extends Alert {
  isSelected?: boolean
  label?: string
  nodeType?: string
}

interface Pagination {
  page: string
  pageSize: number
}

interface AlertsFilters {
  filter: string
  filterValues: string[]
  time: TimeType
  search: string
  pagination: Pagination
  sortAscending: boolean
  sortBy: string
}
