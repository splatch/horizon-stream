import { createTestingPinia } from '@pinia/testing'
import { useApplianceQueries } from '@/store/Queries/applianceQueries'
import { minionsFixture, expectedAppliancesMinions } from '../../fixture/minions'
import { devicesFixture, expectedAppliancesDevices } from '../../fixture/devices'
import { minionLatencyFixture, minionUptimeFixture, deviceLatencyFixture, deviceUptimeFixture } from '../../fixture/metrics'
import { locationsFixture, expectedLocations } from '../../fixture/locations'

describe('Appliances queries', () =>{
  beforeEach(() => {
    createTestingPinia()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('fetched minions, devices, metrics and locations', () => {
    vi.mock('villus', () => ({
      useQuery: vi.fn().mockImplementation(() => ({
        data: {
          value: {
            listMinions: minionsFixture(),    
            minionLatency: minionLatencyFixture(),
            minionUptime: minionUptimeFixture(),
            listDevices: devicesFixture(),
            deviceLatency: deviceLatencyFixture(),
            deviceUptime: deviceUptimeFixture(),
            listLocations: locationsFixture()
          }
        } 
      }))
    }))

    const appliancesQueries = useApplianceQueries()
    expect(appliancesQueries.tableDevices).toStrictEqual(expectedAppliancesDevices)
    expect(appliancesQueries.tableMinions).toStrictEqual(expectedAppliancesMinions)
    expect(appliancesQueries.locations).toStrictEqual(expectedLocations)
  })
})