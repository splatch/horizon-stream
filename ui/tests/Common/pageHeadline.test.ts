import { mount } from '@vue/test-utils'
import PageHeadline from '@/components/Common/PageHeadline.vue'

const defaultHeading = 'Page Headline'
const text = 'New Headline'

let wrapper: any

describe('Page header component', () => {
  afterAll(() => {
    wrapper.unmount()
  })

  it(`should have ${defaultHeading} as heading`, () => {
    wrapper = mount(PageHeadline)

    const componentHeading = wrapper.get('.headline').text()
    expect(componentHeading).toBe(defaultHeading)
  })
  it(`should have ${text} as heading`, () => {
    wrapper = mount(PageHeadline, {
      propsData: {
        text
      }
    })

    const componentHeading = wrapper.get('.headline').text()
    expect(componentHeading).toBe(text)
  })
})
