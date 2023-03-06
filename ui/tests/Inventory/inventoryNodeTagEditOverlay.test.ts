import { mount } from '@vue/test-utils'
import InventoryNodeTagEditOverlay from '@/components/Inventory/InventoryNodeTagEditOverlay.vue'
import { TimeUnit, NodeContent } from '@/types'

const node: NodeContent = {
  id: 1,
  label: 'Monitored Node 1',
  status: '',
  metrics: [
    {
      type: 'latency',
      label: 'Latency',
      value: 9,
      timeUnit: TimeUnit.MSecs,
      status: 'UP'
    },
    {
      type: 'uptime',
      label: 'Uptime',
      value: 1667930274.66,
      timeUnit: TimeUnit.Secs,
      status: 'DOWN'
    },
    {
      type: 'status',
      label: 'Status',
      status: 'DOWN'
    }
  ],
  anchor: {
    profileValue: 75,
    profileLink: 'goto',
    locationValue: 'DefaultMinion',
    locationLink: 'goto',
    managementIpValue: '0.0.0.0',
    managementIpLink: 'goto',
    tagValue: []
  }
}

let wrapper: any

describe('InventoryNodeTagEditOverlay.vue', () => {
  beforeAll(() => {
    wrapper = mount(InventoryNodeTagEditOverlay, {
      props: { node },
      shallow: true
    })
  })

  test('should have required elements', () => {
    const elem = wrapper.get('[data-test="tab-node-checkbox"]')
    expect(elem.exists()).toBe(true)
  })
})
