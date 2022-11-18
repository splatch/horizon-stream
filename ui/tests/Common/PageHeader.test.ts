import { mount } from '@vue/test-utils'
import PageHeader from '@/components/Common/PageHeader.vue'

describe('Page header component', () => {
  it('should have the correct prop heading', () => {
    const heading = 'New Heading'
    
    const wrapper = mount(PageHeader, {
      propsData: {
        heading
      }
    })
    const componentHeading = wrapper.find('.header').text()

    expect(componentHeading).toBe(heading) 
  })
  it('should have the default heading', () => {
    const defaultHeading = 'Page Heading'
    
    const wrapper = mount(PageHeader)
    const componentHeading = wrapper.find('.header').text()

    expect(componentHeading).toBe(defaultHeading) 
  })
})