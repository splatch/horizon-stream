import { Minion } from '@/types/graphql'

const mockData: Minion = {
  id: 'minion-01',
  label: 'minionlabel',
  location: {
    location: 'default',
    id: 1
  },
  lastCheckedTime: 1670542388,
  systemId: 'opennms-minion-8d6f5f64f-4l4wh',
  locationId: 1
}

export const minionFixture = (mockMinion = mockData, props: Partial<Minion> = {}): Minion => ({ ...mockMinion, ...props })
