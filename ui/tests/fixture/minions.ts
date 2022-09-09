import { MinionDto, MinionCollectionDto } from '@/types/graphql'

const mockMinion: MinionDto = {
  'id': 'minion-01',
  'status': 'UP',
  'location': 'Default',
  'lastUpdated': '2022-09-09T12:17:09.497Z'
}
const minionsFixture = (props: Partial<MinionDto> = {}): MinionCollectionDto => ({
  minions: [
    { ...mockMinion, ...props }
  ]
})

const expectedAppliancesMinions = [
  {
    id: 'minion-01',
    status: 'UP',
    location: 'Default',
    lastUpdated: '2022-09-09T12:17:09.497Z',
    icmp_latency: 2,
    snmp_uptime: 97419
  }
]

export {
  minionsFixture,
  expectedAppliancesMinions
}