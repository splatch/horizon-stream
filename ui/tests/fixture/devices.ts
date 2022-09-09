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


const mockMapDevice: DeviceDto = {
  'foreignId': '',
  'foreignSource': '',
  'labelSource': '',
  'location': {
    'latitude': 46.69197463989258,
    'longitude': 2.377929925918579
  },
  'sysContact': '',
  'sysDescription': '',
  'sysLocation': '',
  'sysName': '',
  'sysOid': ''
}

const expectedAppliancesDevices = [
  {
    id: 1,
    label: 'France',
    createTime: '2022-09-07T17:52:51Z',
    managementIp: '127.0.0.1',
    icmp_latency: 0.17,
    snmp_uptime: undefined,
    status: 'DOWN'
  }
]

export {
  devicesFixture,
  mockMapDevice,
  expectedAppliancesDevices
}