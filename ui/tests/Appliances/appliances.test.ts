import mountWithPiniaVillus from 'tests/mountWithPiniaVillus'
import Appliances from '@/containers/Appliances.vue'
import AppliancesNotificationsCtrl from '@/components/Appliances/AppliancesNotificationsCtrl.vue'
import DevicesTable from '@/components/Appliances/AppliancesNodesTable.vue'
import MinionsTable from '@/components/Appliances/AppliancesMinionsTable.vue'
import AppliancesAddNodeCtrl from '@/components/Appliances/AppliancesAddNodeCtrl.vue'
import useKeycloak from '@/composables/useKeycloak'
import Keycloak from 'keycloak-js'
import dateFormatDirective from '@/directives/v-date'

const wrapper = mountWithPiniaVillus({
  type: 'shallow',
  component: Appliances,
  global: {
    directives: {
      date: dateFormatDirective
    }
  }
})

it('should have a header', async () => {
  const { setKeycloak } = useKeycloak()
  await setKeycloak({ authenticated: true } as Keycloak)

  const headerWelcome = wrapper.get('[data-test="header-welcome"]')
  expect(headerWelcome.exists()).toBe(true)
})

it('should have AppliancesNotificationsCtrl component', () => {
  const appliancesNotificationsCtrl = wrapper.getComponent(AppliancesNotificationsCtrl)
  expect(appliancesNotificationsCtrl.exists()).toBe(true)
})

it('should have AppliancesAddNodeCtrl component', () => {
  const appliancesAddNodeCtrl = wrapper.getComponent(AppliancesAddNodeCtrl)
  expect(appliancesAddNodeCtrl.exists()).toBe(true)
})

it('should have DevicesTable component', () => {
  const devicesTable = wrapper.getComponent(DevicesTable)
  expect(devicesTable.exists()).toBe(true)
})

it('should have MinionsTable component', () => {
  const minionsTable = wrapper.getComponent(MinionsTable)
  expect(minionsTable.exists()).toBe(true)
})
