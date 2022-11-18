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
})