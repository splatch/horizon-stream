import { mount } from '@vue/test-utils'
import TextAnchorList from '@/components/Inventory/TextAnchorList.vue'

let wrapper: any

describe('Text anchor list', () => {
  beforeAll(() => {
    wrapper = mount(TextAnchorList)
  })
  afterAll(() => {
    wrapper.unmount() 
  })

  test('component should have been mounted', () => {
    expect(wrapper).toBeTruthy()
  })

  it.skip('should have a list of text anchor list', async () => {
    expect(false).toBe(true)   
  })
})