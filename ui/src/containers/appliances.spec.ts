import { shallowMount } from '@vue/test-utils'
import Appliances from './Appliances.vue'
import DeviceTable from '@/components/Appliances/DeviceTable.vue'

describe('Appliances.vue', () => {
  it('should have DeviceTable component', () => {
    const wrapper = shallowMount(Appliances)
    const deviceTable = wrapper.findComponent(DeviceTable)

    expect(deviceTable.exists()).toBe(true)
  })
})