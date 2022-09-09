import { EventDto, EventCollectionDto } from '@/types/graphql'

const mockEvent: EventDto = {
  'id': 2,
  'label': 'OpenNMS-defined node event: nodeAdded',
  'nodeId': 1,
  'nodeLabel': 'France',
  'severity': 'WARNING',
  'time': '2022-09-07T17:52:51Z'
}
const eventsFixture = (props: Partial<EventDto> = {}): EventCollectionDto => ({
  events: [
    { ...mockEvent, ...props }
  ]
})

export {
  eventsFixture
}