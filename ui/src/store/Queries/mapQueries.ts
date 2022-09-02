import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { ListDevicesForMapDocument } from '@/types/graphql'

export const useMapQueries = defineStore('mapQueries', () => {
  const fetchedDevices = ref()
  const { data } = useQuery({
    query: ListDevicesForMapDocument
  })

  watchEffect(() => {
    fetchedDevices.value = data.value?.listDevices?.devices || []
  })

  return {
    fetchedDevices
  }
})