import { TsResult, TimeSeriesQueryResult } from '@/types/graphql'

const defaultsLatency: TsResult = {
  'metric': {
    '__name__': 'minion_uptime_sec',
    'instance': 'minion-01',
    'job': 'horizon-core',
    'location': 'Default',
    'pushgateway_instance': 'horizon-core-pushgateway'
  },
  'value': [
    1662595962.501,
    22976
  ]
}

export const latencyFixture = (): TimeSeriesQueryResult => {
  return {
    data: { result: [defaultsLatency] }
  }
}

const defaultsUptime: TsResult = {}

export const uptimeFixture = (): TimeSeriesQueryResult => {
  return {
    data: { result: Object.keys(defaultsUptime).length ? [defaultsUptime] : [] }
  }
}