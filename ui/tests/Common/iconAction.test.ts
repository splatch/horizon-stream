import { mount } from '@vue/test-utils'
import IconAction from '@/components/Common/IconAction.vue'
import BubbleChart from '@material-design-icons/svg/outlined/bubble_chart.svg'
import Delete from '@featherds/icon/action/Delete'

let wrapper: any

describe('Icon action component', () => {
  afterAll(() => {
    wrapper.unmount()
  })

  it('should render `div` tag, set viewBox correctly and action called', async () => {
    const item = {
      icon: BubbleChart,
      title: 'Bubble Chart',
      action: vi.fn()
    }
    
    wrapper = mount(IconAction, {
      propsData: {
        item,
        asLi: false 
      }
    }) 
    
    // html tag
    const liTag = wrapper.find('li')
    expect(liTag.exists()).toBe(false)
    
    // viewBox
    const iconProps = wrapper.props().item.icon.render().props
    const viewBoxValue = `0 0 ${iconProps.width} ${iconProps.height}`
    const attrViewBoxValue = wrapper.findComponent('svg').attributes('viewBox')
    expect(attrViewBoxValue).toBe(viewBoxValue)

    // action
    await wrapper.findComponent('svg').trigger('click')
    expect(wrapper.props().item.action).toHaveBeenCalledOnce()
  })
  
  it('should render `li` tag, set viewBox correctly and action called', async () => {
    const item = {
      icon: Delete,
      title: 'Delete',
      action: vi.fn()
    }
    
    wrapper = mount(IconAction, {
      propsData: {
        item,
        asLi: true 
      }
    }) 
    
    // html tag
    const liTag = wrapper.find('li')
    expect(liTag.exists()).toBe(true)
    
    // viewBox
    const iconProps = wrapper.props().item.icon.render().props
    const viewBoxValue = iconProps.viewBox
    const attrViewBoxValue = wrapper.findComponent('svg').attributes('viewBox')
    expect(attrViewBoxValue).toBe(viewBoxValue)

    // action
    await wrapper.findComponent('svg').trigger('click')
    expect(wrapper.props().item.action).toHaveBeenCalledOnce()
  })
})