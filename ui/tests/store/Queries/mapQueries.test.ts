import { createTestingPinia } from '@pinia/testing'
import { useMapQueries } from '@/store/Queries/mapQueries'
import { devicesFixture, mockMapDevice } from '../../fixture/devices'

describe('Map queries', () =>{
  beforeEach(() => {
    createTestingPinia()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('fetched map devices', () => {
    vi.mock('villus', () => ({
      useQuery: vi.fn().mockImplementation(() => ({
        data: {
          value: {
            listDevices: {
              devices: devicesFixture(mockMapDevice)
            }
          }
        }
      }))
    }))

    const mapQueries = useMapQueries()
    expect(mapQueries.devices).toStrictEqual(devicesFixture(mockMapDevice))
  })
})