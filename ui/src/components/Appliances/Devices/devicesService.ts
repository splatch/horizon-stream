import { api } from '@/services/axiosInstances'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import { getMsgFromError } from '@/services/errorService'
import { defaultDevice, Device } from './devicesTypes'
import { transformDeviceList } from './devicesBff'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const endpoint = {
  default: '/get:devices',
  400: '/get:devices:400',
  404: '/get:devices:404'
}

/**
 * Return list of devices, or show error message in case of error on API request
 * 
 * @returns A list of devices
 */
const getDevices = async (): Promise<Device[]> => {
  startSpinner()

  try {
    const { data } = await api.get(endpoint['default'])
    // const { data } = await api.get(endpoint[400])
    // const { data } = await api.get(endpoint[404])

    return transformDeviceList(data)
  } catch (err: unknown) {
    showSnackbar({ error: true, msg: getMsgFromError(err) })
    
    return defaultDevice.list
  } finally {
    stopSpinner()
  }
}

export { getDevices }
