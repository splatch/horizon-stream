import { shallowMount } from '@vue/test-utils'
import Filter from '@/components/Inventory/Filter.vue'

let wrapper: any

describe('Filter component', () => {
  beforeAll(() => {
    wrapper = shallowMount(Filter)
  })
  afterAll(() => {
    wrapper.unmount()
  })

  describe('Required components', () => {
    const requiredComponents = [
      // 'search',
      'select-node'
    ]

    it.each(requiredComponents)('should have "%s" component', (item) => {
      expect(wrapper.get(`[data-test="${item}"]`).exists()).toBe(true)
    })
  })
})