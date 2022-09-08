import { EventDto, EventCollectionDto } from '@/types/graphql'

const defaultsEvents: EventDto = {
  'id': 2,
  'label': 'OpenNMS-defined node event: nodeAdded',
  'nodeId': 1,
  'nodeLabel': 'France',
  'severity': 'WARNING',
  'time': '2022-09-07T17:52:51Z'
}

export const eventsFixture = (): EventCollectionDto => {
  return {
    events: [defaultsEvents]
  }
}