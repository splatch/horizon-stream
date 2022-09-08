import { setActivePinia, createPinia } from 'pinia'
import { useEventsQueries } from '@/store/Queries/eventsQueries'
import { eventsFixture } from '../../fixture/events'
import { devicesFixture } from '../../fixture/devices'
import { latencyFixture, uptimeFixture } from '../../fixture/metrics'

describe('Events Queries', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('fetches events', async () => {
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
    
    const eventsQueries = useEventsQueries()
    const expectedData = {
      ...eventsFixture(),
      ...devicesFixture(),
      deviceLatency: (latencyFixture()).data?.result,
      deviceUptime: (uptimeFixture()).data?.result
    }
    expect(eventsQueries.fetchedData).toStrictEqual(expectedData)
  })
})