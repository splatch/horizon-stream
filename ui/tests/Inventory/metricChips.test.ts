import { mount } from '@vue/test-utils'
import MetricChips from '@/components/Inventory/MetricChips.vue'

let wrapper: any

describe('Metric chip list', () => {
  beforeAll(() => {
    wrapper = mount(MetricChips)
  })
  afterAll(() => {
    wrapper.unmount() 
  })

  test('component should have been mounted', () => {
    expect(wrapper).toBeTruthy()
  })

  it.skip('should have a list of metric chip', async () => {
    const icon = wrapper.find('[data-test="line-chart-icon"]')

    await icon.trigger('click')

    expect(false).toBe(true)   
  })
})