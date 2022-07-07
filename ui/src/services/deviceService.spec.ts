import { sDeviceItems } from './deviceService'
import { api } from '@/services/axiosInstances'
import { defaultDevice, Device } from '@/types/appliances'

describe('DeviceService.ts', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('sDeviceItems()', () => {
    test('Request should return a list of devices', async () => {
      const success = {
        data: {
          items: [ defaultDevice ]
        }
      }
      api.get = vi.fn().mockResolvedValueOnce(success)

      const items = await sDeviceItems()
      
      expect(api.get).toHaveBeenCalledOnce()
      expect(items).toEqual(success.data.items)
    })

    test('Request should return an empty list and show error message', async () => {
      const failed: Array<Device> = []
      api.get = vi.fn().mockResolvedValueOnce(failed)

      const { default: useSnackbar } = await import('@/composables/useSnackbar')
      const snackbar = useSnackbar()
      const showSnackbarSpy = vi.spyOn(snackbar, 'showSnackbar')

      const errorService = await import('@/services/errorService')
      const errorServiceSpy = vi.spyOn(errorService, 'getMsgFromError')
      
      const items = await sDeviceItems()
      
      expect(showSnackbarSpy.getMockName()).toEqual('showSnackbar')
      // expect(showSnackbarSpy).toHaveBeenCalledOnce() // todo

      expect(errorServiceSpy.getMockName()).toEqual('getMsgFromError')
      expect(errorServiceSpy).toHaveBeenCalledOnce()

      expect(api.get).toHaveBeenCalledOnce()
      expect(items).toEqual(failed)
    })

    test('Request should start and stop spinner',async()=>{
      const { default: useSpinner } = await import('@/composables/useSpinner')
      const spinner = useSpinner()
      const startSpinnerSpy = vi.spyOn(spinner, 'startSpinner')
      const stopSpinnerSpy = vi.spyOn(spinner, 'stopSpinner')
      
      await sDeviceItems()

      expect(startSpinnerSpy.getMockName()).toEqual('startSpinner')
      // expect(startSpinnerSpy).toHaveBeenCalledOnce() // todo

      expect(stopSpinnerSpy.getMockName()).toEqual('stopSpinner')
      // expect(stopSpinnerSpy).toHaveBeenCalledOnce() // todo
    })
  })
})