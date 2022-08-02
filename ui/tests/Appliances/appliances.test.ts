import { mount } from '@vue/test-utils'
import Appliances from '@/containers/Appliances.vue'
import NotificationsCtrl from '@/components/Appliances/NotificationsCtrl.vue'
import DeviceTable from '@/components/Appliances/DeviceTable.vue'
import MinionsTable from '@/components/Appliances/MinionsTable.vue'
import { createPinia } from 'pinia'
import { useAppliancesStore } from '@/store/Views/appliancesStore'
import { createClient, VILLUS_CLIENT } from 'villus'

const wrapper = mount(Appliances, { 
  global: { 
    plugins: [ createPinia() ],
    provide: {
      [VILLUS_CLIENT as unknown as string]: createClient({
        url: 'http://test/graphql'
      })
    }
  }
})

describe('Appliances.vue', () => {
  it('should have NotificationsCtrl component', () => {
    const notificationsCtrl = wrapper.findComponent(NotificationsCtrl)
    expect(notificationsCtrl.exists()).toBe(true)
  })

  it('should have DeviceTable component', () => {
    const deviceTable = wrapper.findComponent(DeviceTable)
    expect(deviceTable.exists()).toBe(true)
  })

  it('should have MinionsTable component', () => {
    const minionsTable = wrapper.findComponent(MinionsTable)
    expect(minionsTable.exists()).toBe(true)
  })
})

test('The appliancesStore pinia functions should be called', async () => {
  const store = useAppliancesStore()

  const hideMinionsBtn = wrapper.get('[data-test="hide-minions-btn"]')
  await hideMinionsBtn.trigger('click')
  expect(store.minionsTableOpen).toBe(false)
  
  const showMinionsBtn = wrapper.get('[data-test="show-minions-btn"]')
  await showMinionsBtn.trigger('click')
  expect(store.minionsTableOpen).toBe(true)
})
