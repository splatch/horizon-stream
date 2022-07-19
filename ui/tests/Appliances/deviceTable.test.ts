import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import { createClient, setActiveClient } from 'villus'
import DeviceTable from '@/components/Appliances/DeviceTable.vue'
import { setActivePinia } from 'pinia'

describe('DeviceTable.vue', () => {
  beforeEach(() => {
    const deviceItems = computed(() => [{
      id: '1',
      name: 'device1',
      icmp_latency: 'latency1',
      snmp_uptime: 'uptime1',
      status: 'status1'
    }]) 

    setActivePinia(createTestingPinia({
      initialState: { 
        deviceQueries: { 
          listDevices: deviceItems
        }
      }
    }))

    setActiveClient(createClient({
      url: 'http://test/graphql'
    }))
  })

  it('should have an empty table when there\'s no device', () =>{
    const wrapper = mount(DeviceTable, { 
      global: { plugins: [createTestingPinia()] } // we need to find a way to be able to set the state
    })
      
    const deviceItem = wrapper.find('[data-test="device-item"]')
    expect(deviceItem.exists()).toBe(false)
  })
    
  it('should display a list when there\'s device', () => {
    const wrapper = mount(DeviceTable)
    const deviceItem = wrapper.find('[data-test="device-item"]')
    expect(deviceItem.exists()).toBe(true)
  })
})