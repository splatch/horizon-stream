import { mount } from '@vue/test-utils'
import { FeatherSelect } from '@featherds/select'
import SelectDropdown from '@/components/Common/SelectDropdown.vue'

let wrapper: any

describe('Select Dropdown component', () => {
  const onSelect = (selectedItem: any) => {
    wrapper.vm.$emit('selectedItem', selectedItem)
  }
  const nodeType = {
    label: 'Node Type',
    options: [
      {
        id: 1,
        type: 'type1'
      },
      {
        id: 2,
        type: 'type2'
      },
      {
        id: 3,
        type: 'type3'
      }
    ],
    optionText: 'type',
    cb: onSelect
  }

  beforeEach(() => {
    wrapper = mount(SelectDropdown, {
      propsData: {
        selectType: nodeType
      }
    })
  })
  afterAll(() => {
    wrapper.unmount() 
  })

  it('should emit `selectedItem` event and option value when selecting an item', async () => {
    const selectInput = wrapper.getComponent(FeatherSelect).get('.feather-select-input')
  
    await selectInput.trigger('focus')
    await selectInput.trigger('keydown', { keyCode: 'Enter' })
    await selectInput.trigger('keydown', { keyCode: 'Escape' })

    expect(wrapper.emitted()).toHaveProperty('selectedItem')
    expect(wrapper.emitted().selectedItem[0][0]).toEqual(nodeType.options[0])
  })

  it('should emit `selectedItem` event and option value when selecting an item', async () => {
    const selectInput = wrapper.getComponent(FeatherSelect).get('.feather-select-input')
  
    await selectInput.trigger('focus')
    await selectInput.trigger('keydown', { keyCode: 'ArrowDown' })
    await selectInput.trigger('keydown', { keyCode: 'ArrowDown' })
    await selectInput.trigger('keydown', { keyCode: 'Enter' })

    expect(wrapper.emitted()).toHaveProperty('selectedItem')
    expect(wrapper.emitted().selectedItem[0][0]).toEqual(nodeType.options[1])
  })
})