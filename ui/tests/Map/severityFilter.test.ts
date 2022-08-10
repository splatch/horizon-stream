import SeverityFilter from '@/components/Map/SeverityFilter.vue'
import { useMapStore } from '@/store/Views/mapStore'
import setupWrapper from '../helpers/setupWrapper'

let wrapper: any

beforeEach(() => {
  wrapper = setupWrapper({
    component: SeverityFilter
  })
})

test('select input item should set `selectedSeverity` in store', async () => {
  const expectedValue = 'MINOR'
  const mapStore = useMapStore()
  const severitySelectInput = wrapper.get('.feather-select-input')
  
  await severitySelectInput.setValue(expectedValue)
  
  expect(mapStore.selectedSeverity).toBe(expectedValue)
})