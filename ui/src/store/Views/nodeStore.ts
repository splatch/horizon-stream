import { defineStore } from 'pinia'
import { useEventsQueries } from '@/store/Queries/eventsQueries'

export const useNodeStore = defineStore('nodeStore', () => {
  const eventsQueries = useEventsQueries()

  const fetchedData = computed(() => eventsQueries.fetchedData)

  return {
    fetchedData
  }
})