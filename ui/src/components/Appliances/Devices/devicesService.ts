import { api } from '@/services/axiosInstances'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import { getMsgFromError } from '@/services/errorService'
import { defaultDevices, Device } from './devicesTypes'
import { transformDeviceItems } from './devicesBff'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const endpoint = {
  default: '/get:devices',
  empty: '/get:devices:empty',
  400: '/get:devices:400', // custom error message: Request failed.
  404: '/get:devices:404' // fallback error message: Could not complete request.
}

/**
 * Return list of devices, or show error message in case of error on API request
 * 
 * @returns A list of devices
 */
const sDeviceItems = async (): Promise<Device[]> => {
  startSpinner() 

  try {
    const { data } = await api.get(endpoint['default'])
    // const { data } = await api.get(endpoint['empty'])
    // const { data } = await api.get(endpoint[400])
    // const { data } = await api.get(endpoint[404])

    return transformDeviceItems(data)
  } catch (err: unknown) {
    showSnackbar({ error: true, msg: getMsgFromError(err) })

    return defaultDevices.items
  } finally {
    stopSpinner()
  }
}

export { sDeviceItems }
