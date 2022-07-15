// @ts-nocheck
import casual from 'casual'

const rndNumber = () => Math.floor(Math.random() * 100)

casual.define('minion', function () {
  return {
    id: casual.uuid,
    label: casual.word,
    status: casual.safe_color_name,
    location: casual.city,
    date: casual.date(),
    latency: rndNumber(),
    uptime: casual.unix_time
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