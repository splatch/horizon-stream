import { shallowMount, mount } from '@vue/test-utils'
import Appliances from '@/containers/Appliances.vue'
import NotificationsCtrl from '@/components/Appliances/NotificationsCtrl.vue'
import DeviceTable from '@/components/Appliances/DeviceTable.vue'
import MinionsTable from '@/components/Appliances/MinionsTable.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useAppliancesStore } from '@/store/Views/appliancesStore'
import { createClient, setActiveClient } from 'villus'

describe('Appliances.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())

    setActiveClient(createClient({
      url: 'http://test/graphql'
    }))
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

test('The appliancesStore pinia functions should be called', async () => {
  const wrapper = mount(Appliances)
  const store = useAppliancesStore()

  const hideMinionsBtn = wrapper.get('[data-test="hide-minions-btn"]')
  await hideMinionsBtn.trigger('click')
  expect(store.minionsTableOpen).toBe(false)
  
  const showMinionsBtn = wrapper.get('[data-test="show-minions-btn"]')
  await showMinionsBtn.trigger('click')
  expect(store.minionsTableOpen).toBe(true)
})
