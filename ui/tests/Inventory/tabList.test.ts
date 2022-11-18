import { mount } from '@vue/test-utils'
import { FeatherTab } from '@featherds/tabs'
import TabList from '@/components/Inventory/TabList.vue'
import Card from '@/components/Inventory/Card.vue'

const tabs = [
  {
    label: 'Monitored Nodes',
    nodes: [
      {
        header: 'Node 1'
      },
      {
        header: 'Node 11'
      }
    ]
  },
  {
    label: 'Unmonitored Nodes',
    nodes: [
      {
        header: 'Node 2'
      },
      {
        header: 'Node 22'
      }
    ]
  },
  {
    label: 'Detected Nodes',
    nodes: [
      {
        header: 'Node 3'
      },
      {
        header: 'Node 33'
      }
    ]
  }
]

let wrapper: any

describe('Inventory node tab list', () => {
  beforeAll(() => {
    wrapper = mount(TabList, {
      props: {
        tabs
      }
    })
  })
  afterAll(() => {
    wrapper.unmount() 
  })

  it(`should have ${tabs.length} tabs`, () => {
    const featherTabs = wrapper.findAllComponents(FeatherTab)
    expect(featherTabs.length).toEqual(tabs.length)
  })

  const expectedNodesLength = tabs.reduce((acc, tab) => acc += tab.nodes.length, 0)

  it(`should have ${expectedNodesLength} nodes`, () => {
    const cards = wrapper.findAllComponents(Card)
    expect(cards.length).toEqual(expectedNodesLength)
  })
})