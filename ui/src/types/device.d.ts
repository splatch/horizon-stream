import { DeviceDto } from "./graphql"

interface ExtendedDeviceDTO extends DeviceDto {
  icmp_latency: string
  snmp_uptime: string
  status: string
}
