import { TsResult } from '@/types/graphql'

interface ExtendedTsResult extends TsResult {
  values: Array<number, string>[]
}

type DataSets = ExtendedTsResult[][]

interface GraphMetric {
  label: string,
  metrics: Array<string>,
  id: string,
  location: string
}