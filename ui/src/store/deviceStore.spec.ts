import { setActivePinia, createPinia } from 'pinia'
import { useDeviceStore } from './deviceStore'
import deviceService from '@/services/deviceService'

describe('deviceStore.ts', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setActivePinia(createPinia())
  })

  test('When request succeeded, it sets device\'s list to deviceItems', async () => {
    const success = [{
      id: '1',
      name: 'device1',
      icmp_latency: 'latency1',
      snmp_uptime: 'uptime1'
    }]
    const spy = vi.spyOn(deviceService, 'sDeviceItems')
    spy.mockResolvedValueOnce(success)

    const store = useDeviceStore()
    await store.aGetDevices()

    expect(store.deviceItems).toMatchObject(success)
  })
  
  test('When request failed, it sets empty list to deviceItems', async () => {
    const failed: any[] = []
    const spy = vi.spyOn(deviceService, 'sDeviceItems')
    spy.mockResolvedValueOnce(failed)

    const store = useDeviceStore()
    await store.aGetDevices()

    expect(store.gDeviceItems).toMatchObject(failed)
  })
})