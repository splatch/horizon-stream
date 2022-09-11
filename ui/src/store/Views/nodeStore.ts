import { defineStore } from 'pinia'
import { useNodeStatusQueries } from '@/store/Queries/nodeStatusQueries'

export const useNodeStore = defineStore('nodeStore', () => {
  const eventsQueries = useNodeStatusQueries()

  const fetchedEvents = computed(() => eventsQueries.fetchedEvents)

  return {
    fetchedEvents
  }
})