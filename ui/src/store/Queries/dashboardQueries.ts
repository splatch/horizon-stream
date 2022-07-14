import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { DashboardDocument, DashboardQuery } from '@/types/graphql'

export const useDashboardQueries = defineStore('dashboardQueries', () => {
  const { data, execute } = useQuery<DashboardQuery>({
    query: DashboardDocument
  })

  const alarms = computed(() => data.value?.listAlarms?.alarms || [])

  return {
    alarms,
    fetch: execute
  }
})
