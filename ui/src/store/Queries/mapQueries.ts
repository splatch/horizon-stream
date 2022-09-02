import { defineStore } from 'pinia'
import { useQuery } from 'villus'
import { DeviceForMapDocument } from '@/types/graphql'

export const useMapQueries = defineStore('mapQueries', () => {
  const fetchedDevices = ref()
  const { data } = useQuery({
    query: DeviceForMapDocument
  })

  watchEffect(() => {
    fetchedDevices.value = data.value?.listDevices?.devices || []
  })

  return {
    fetchedDevices
  }
})