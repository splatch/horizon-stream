import { MinionDto } from "./graphql"

interface ExtendedMinionDTO extends MinionDto {
  icmp_latency: number
  snmp_uptime: number
}
