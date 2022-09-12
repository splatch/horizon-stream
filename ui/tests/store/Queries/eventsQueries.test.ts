import { createTestingPinia } from '@pinia/testing'
import { useNodeStatusQueries } from '@/store/Queries/nodeStatusQueries'
import { eventsFixture } from '../../fixture/events'
import { devicesFixture } from '../../fixture/devices'
import { latencyFixture, uptimeFixture } from '../../fixture/metrics'

describe('Events Queries', () => {
  beforeEach(() => {
    createTestingPinia()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('fetches events, devices and metrics', () => {
    vi.mock('villus', () => ({
      useQuery: vi.fn().mockImplementation(() => ({
        data: { 
          value: { 
            listEvents: eventsFixture(),
            listDevices: devicesFixture(),
            deviceLatency: latencyFixture(),
            deviceUptime: uptimeFixture()
          }}
      }))
    }))
    
    const nodeStatusQueries = useNodeStatusQueries()
    const expectedFetchedData = {
      ...eventsFixture(),
      ...devicesFixture(),
      deviceLatency: (latencyFixture()).data?.result,
      deviceUptime: (uptimeFixture()).data?.result
    }
    expect(nodeStatusQueries.fetchedData).toStrictEqual(expectedFetchedData)
  })
})