import { Device, Devices, defaultDevice } from './devicesTypes'

/**
 * transform data from API response to be consumed by FE
 * 
 * @param data 
 * @returns List of devices
 */
export const transformDeviceList = (data: Devices): Array<Device> => {
  const list = data.list || defaultDevice.list

  // transform data if needed

  return list
}