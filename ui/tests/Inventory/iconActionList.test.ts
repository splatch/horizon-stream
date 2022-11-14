import { mount } from '@vue/test-utils'
import IconActionList from '@/components/Inventory/IconActionList.vue'

let wrapper: any

describe('Inventory node icon action list', () => {
  beforeAll(() => {
    wrapper = mount(IconActionList)
  })
  afterAll(() => {
    wrapper.unmount() 
  })

  test('component should have been mounted', () => {
    expect(wrapper).toBeTruthy()
  })

  describe.skip('Method called on action click', () => {
    test('line chart method should have been called', async () => {
      const icon = wrapper.find('[data-test="line-chart-icon"]')

      await icon.trigger('click')

      expect(false).toBe(true)   
    })
  })
})