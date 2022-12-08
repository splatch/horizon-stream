import { Minion } from '@/types/graphql'

const mockData: Minion = {
  id: 'minion-01',
  label: 'minionlabel',
  location: {
    location: 'default',
    id: 1
  },
  lastCheckedTime: '2022-09-09T12:17:09.497Z',
  systemId: '123',
  locationId: 1
}

export const minionFixture = (mockMinion = mockData, props: Partial<Minion> = {}): Minion => ({ ...mockMinion, ...props })
