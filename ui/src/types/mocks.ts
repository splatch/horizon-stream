import { EventDtoInput } from '@/graphql/operations'
import { add } from 'date-fns'

const mockEvent: EventDtoInput = {
  uei: 'uei.opennms.org/alarms/trigger',
  source: 'kanata-office',
  time: add(new Date(), { days: 1 }).toISOString(),
  description: 'A problem has been triggered...'
}

export const getMockEvent = (): EventDtoInput => mockEvent
