import { Device, defaultDevices } from './devicesTypes'

/**
 * Transform data from API response to be consumed by FE
 * 
 * @param data 
 * @returns List of devices
 */
export const transformDeviceItems = (data: any): Array<Device> => {
  let items = defaultDevices.items

  if(data.items.length) {
    // transform data if needed
    items = data.items
  }

  return items
}