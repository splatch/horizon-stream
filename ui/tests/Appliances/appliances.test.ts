import { shallowMount } from '@vue/test-utils'
import Appliances from '@/containers/Appliances.vue'
import NotificationsCtrl from '@/components/Appliances/NotificationsCtrl.vue'
import DeviceTable from '@/components/Appliances/DeviceTable.vue'
import MinionsTable from '@/components/Appliances/MinionsTable.vue'
import { setActivePinia } from 'pinia'
import { createTestingPinia } from '@pinia/testing'

describe('Appliances.vue', () => {
  beforeEach(() => {
    setActivePinia(createTestingPinia({}))
  })

  it('should have NotificationsCtrl component', () => {
    const wrapper = shallowMount(Appliances)
    const notificationsCtrl = wrapper.findComponent(NotificationsCtrl)

    expect(notificationsCtrl.exists()).toBe(true)
  })
  it('should have DeviceTable component', () => {
    const wrapper = shallowMount(Appliances)
    const deviceTable = wrapper.findComponent(DeviceTable)

    expect(deviceTable.exists()).toBe(true)
  })
  it('should have MinionsTable component', () => {
    const wrapper = shallowMount(Appliances)
    const minionsTable = wrapper.findComponent(MinionsTable)

    expect(minionsTable.exists()).toBe(true)
  })
})
