// @ts-nocheck
import casual from 'casual'
import { rndNumber, rndStatus, rndLatency, rndUptime } from '../helpers/random'

casual.define('minion', function () {
  return {
    id: casual.uuid,
    label: casual.word,
    status: rndStatus(),
    location: casual.city,
    date: casual.date(),
    icmp_latency: rndLatency(),
    snmp_uptime: rndUptime()
  }
})

casual.define('listMinions', function () {
  return {
    items: [casual.minion, casual.minion, casual.minion, casual.minion, casual.minion],
    count: rndNumber(),
    totalCount: rndNumber(),
    offset: rndNumber()
  }
})

const minion = casual.minion
const listMinions = casual.listMinions

export {
  minion,
  listMinions
}