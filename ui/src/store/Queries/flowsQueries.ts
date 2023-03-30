import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import {
  FindApplicationsDocument,
  FindApplicationSeriesDocument,
  FindApplicationSummariesDocument
} from '@/types/graphql'

export const useflowsQueries = defineStore('flowsQueries', {
  state: () => {
    const getApplicationsSeries = (count = 10, step = 3600000, startTime = 1670661637000, endTime = 1679910407693) =>
      useQuery({
        query: FindApplicationSeriesDocument,
        variables: {
          count,
          step,
          startTime,
          endTime
        }
      })

    const getApplications = (count = 10, startTime = 1670661637000, endTime = 1679910407693) =>
      useQuery({
        query: FindApplicationsDocument,
        variables: {
          count,
          startTime,
          endTime
        }
      })

    const getApplicationsSummaries = (count = 10, startTime = 1670661637000, endTime = 1679910407693) =>
      useQuery({
        query: FindApplicationSummariesDocument,
        variables: {
          count,
          startTime,
          endTime
        }
      })

    return {
      getApplicationsSeries,
      getApplicationsSummaries,
      getApplications
    }
  }
})
