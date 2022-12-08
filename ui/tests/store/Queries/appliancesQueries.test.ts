import { createTestingPinia } from '@pinia/testing'
import { useAppliancesQueries } from '@/store/Queries/appliancesQueries'
import { minionFixture } from '../../fixture/minions'
import { nodeFixture } from '../../fixture/nodes'
import { minionLatencyFixture, minionUptimeFixture, deviceLatencyFixture, deviceUptimeFixture } from '../../fixture/metrics'
import { locationsFixture, expectedLocations } from '../../fixture/locations'

describe('Appliances queries', () =>{
  beforeEach(() => {
    createTestingPinia()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('fetched minions, nodes, metrics and locations', () => {
    vi.mock('villus', () => ({
      useQuery: vi.fn().mockImplementation(() => ({
        data: {
          value: {
            findAllMinions: [ minionFixture() ],
            minionLatency: minionLatencyFixture(),
            minionUptime: minionUptimeFixture(),
            findAllNodes:  [ nodeFixture() ],
            deviceLatency: deviceLatencyFixture(),
            deviceUptime: deviceUptimeFixture(),
            findAllLocations: locationsFixture()
          }
        } 
      }))
    }))
    
    const expectedAppliancesMinions = [
      {
        id: 'minion-01',
        label: 'minionlabel',
        location: {
          location: 'default',
          id: 1
        },
        lastCheckedTime: '2022-09-09T12:17:09.497Z',
        systemId: '123',
        locationId: 1,
        icmp_latency: 2,
        snmp_uptime: 97419
      }
    ]

    const expectedAppliancesDevices = [
      {
        id: 1,
        nodeLabel: 'France',
        monitoringLocationId: 1,
        createTime: '2022-09-07T17:52:51Z'
      }
    ]

    const appliancesQueries = useAppliancesQueries()
    expect(appliancesQueries.tableMinions).toStrictEqual(expectedAppliancesMinions)
    expect(appliancesQueries.tableNodes).toStrictEqual(expectedAppliancesDevices)
    expect(appliancesQueries.locations).toStrictEqual(expectedLocations)
  })
})