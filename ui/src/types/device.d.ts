
import { BGColors } from "@/components/Appliances/appliances.helpers"
import { DeviceDto } from "./graphql"

interface ExtendedDeviceDTO extends DeviceDto {
  icmp_latency: string
  snmp_uptime: string
  status: string
}

interface ExtendedDeviceDTOWithBGColors extends ExtendedDeviceDTO, BGColors {}
