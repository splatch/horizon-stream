import { TsResult } from '@/types/graphql'

interface ExtendedTsResult extends TsResult {
  values: Array<number, string>[]
}

type DataSets = ExtendedTsResult[][]
