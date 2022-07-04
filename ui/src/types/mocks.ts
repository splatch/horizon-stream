import { EventDtoInput } from '@/graphql/generatedTypes'
import { add } from 'date-fns'

const mockEvent: EventDtoInput = {
  uei: 'uei.opennms.org/alarms/trigger',
  time: add(new Date(), { days: 1 }).toISOString(),
  source: 'asn-cli-script',
}

export const getMockEvent = (): EventDtoInput => mockEvent
