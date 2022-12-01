import { mount } from '@vue/test-utils'
import TextAnchorList from '@/components/Inventory/TextAnchorList.vue'

let wrapper: any

describe('Text anchor list', () => {
  beforeAll(() => {
    wrapper = mount(TextAnchorList, {
      shallow: true,
      props: {
        anchor: {
          profileValue: 75,
          profileLink: '#',
          locationValue: 'DefaultMinion',
          locationLink: '#',
          ipAddressValue: 25,
          ipAddressLink: '#',
          tagValue: 100,
          tagLink: '#'
        }
      }
    })
  })
  afterAll(() => {
    wrapper.unmount() 
  })

  const anchorList = [
    'profile',
    'location',
    'ip-address',
    'tag'
  ]
  it.each(anchorList)('should have "%s" element', (elem) => {
    expect(wrapper.get(`[data-test="${elem}"]`).exists()).toBe(true)
  })
})