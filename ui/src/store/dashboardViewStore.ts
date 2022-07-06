import { useQuery } from 'villus'
import { defineStore } from 'pinia'
import { DashboardDocument, DashboardQuery } from '@/graphql/operations'

export const useDashboardViewStore = defineStore('dashboardViewStore', () => {
  const { data, execute } = useQuery<DashboardQuery>({
    query: DashboardDocument
  })

  const alarms = computed(() => data.value?.listAlarms?.alarms || [])

  return {
    alarms,
    fetch: execute
  }
})
