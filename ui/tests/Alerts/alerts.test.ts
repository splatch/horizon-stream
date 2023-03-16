import mount from '../mountWithPiniaVillus'
import Alerts from '@/containers/Alerts.vue'

let wrapper: any

describe('Alerts', () => {
  beforeAll(() => {
    wrapper = mount({
      component: Alerts,
      shallow: true
    })
  })
  afterAll(() => {
    wrapper.unmount()
  })

  test('Mount', () => {
    expect(wrapper).toBeTruthy()
  })

  test('Should have the required elements', () => {
    const elems = ['headline', 'clear-all-filters-btn', 'severity-filter', 'sort-date', 'search-filter', 'card-list']
    elems.forEach((elem) => {
      const el = wrapper.get(`[data-test="${elem}"]`)
      expect(el.exists()).toBeTruthy()
    })
  })
})
