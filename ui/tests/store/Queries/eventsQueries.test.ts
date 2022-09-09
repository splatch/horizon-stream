import { createTestingPinia } from '@pinia/testing'
import { useEventsQueries } from '@/store/Queries/eventsQueries'
import { eventsFixture } from '../../fixture/events'
import { devicesFixture } from '../../fixture/devices'
import { deviceLatencyFixture, deviceUptimeFixture } from '../../fixture/metrics'

describe('Events queries', () => {
  beforeEach(() => {
    createTestingPinia()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('fetched events, devices and metrics', () => {
    vi.mock('villus', () => ({
      useQuery: vi.fn().mockImplementation(() => ({
        data: { 
          value: { 
            listEvents: eventsFixture(),
            listDevices: devicesFixture(),
            deviceLatency: deviceLatencyFixture(),
            deviceUptime: deviceUptimeFixture()
          }}
      }))
    }))
    
    const eventsQueries = useEventsQueries()
    const expectedFetchedEvents = {
      ...eventsFixture(),
      ...devicesFixture(),
      deviceLatency: (deviceLatencyFixture()).data?.result,
      deviceUptime: (deviceUptimeFixture()).data?.result
    }
    expect(eventsQueries.fetchedEvents).toStrictEqual(expectedFetchedEvents)
  })
})