import { DeviceDto, DeviceCollectionDto } from '@/types/graphql'

const mockDevice: DeviceDto = {
  'id': 1,
  'label': 'France',
  'createTime': '2022-09-07T17:52:51Z',
  'managementIp': '127.0.0.1'
}
const devicesFixture = (props: Partial<DeviceDto> = {}): DeviceCollectionDto => ({
  devices: [
    { ...mockDevice, ...props }
  ]
})

export {
  devicesFixture
}