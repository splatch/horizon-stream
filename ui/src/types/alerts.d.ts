import { Alert } from './graphql'
import { TimeType } from '@/components/Alerts/alerts.constant'

interface IAlert extends Alert {
  isSelected?: boolean
  label?: string
  nodeType?: string
}

interface AlertsFilter {
  severity: [number]
  time: TimeType | undefined
  search: string
}
