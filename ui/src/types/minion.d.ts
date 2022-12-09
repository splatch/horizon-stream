import { BGColors } from '@/components/Appliances/utils'
import { Minion } from './graphql'

interface ExtendedMinionDTO extends Minion {
  icmp_latency: number
  snmp_uptime: number
  status: string
}

interface ExtendedMinionDTOWithBGColors extends ExtendedMinionDTO, BGColors {}