import mount from 'tests/mountWithPiniaVillus'
import AlertsCardList from '@/components/Alerts/AlertsCardList.vue'
import { useAlertsStore } from '@/store/Views/alertsStore'
// import { getAlertsList } from '../../mock-graphql/src/fixture/alerts.fixture'
import { getAlertsList } from '../fixture/alerts'

let wrapper: any

describe('Alerts list', () => {
  /* beforeEach(() => {
    wrapper = mount({
      component: AlertsCardList,
      props: {
        alerts: [] //getAlertsList()
      }
    })
  }) */
  afterAll(() => {
    wrapper.unmount()
  })

  test('Mount', () => {
    wrapper = mount({
      component: AlertsCardList,
      props: {
        alerts: [] //getAlertsList()
      }
    })

    expect(wrapper).toBeTruthy()
  })

  test('Should have alerts list if list not empty', async () => {
    wrapper = mount({
      component: AlertsCardList,
      props: {
        alerts: getAlertsList()
      }
    })

    const alertsStore = useAlertsStore()
    alertsStore.alertsList = getAlertsList()
    await wrapper.vm.$nextTick()

    const elem = wrapper.find('[data-test="alerts-list"]')
    expect(elem.exists()).toBeTruthy()

    // wrapper.unmount()
  })

  describe('Alerts list empty', () => {
    beforeAll(() => {
      wrapper = mount({
        component: AlertsCardList,
        props: {
          alerts: [] //getAlertsList()
        }
      })
    })
    /* afterAll(() => {
      wrapper.unmount()
    }) */

    test('Should not have alerts list if list empty', () => {
      const elem = wrapper.find('[data-test="empty-list"]')
      expect(elem.exists()).toBeTruthy()
    })

    test('Should have a message', () => {
      const elem = wrapper.get('[data-test="msg"]')
      expect(elem.exists()).toBeTruthy()
    })

    test('Should have clear all filters button', async () => {
      const btn = wrapper.find('[data-test="clear-all-filters-btn"]')
      expect(btn.exists()).toBeTruthy()

      // TODO not working
      // const alertsStore = useAlertsStore()
      // const spy = vi.spyOn(alertsStore, 'clearAllFilters')
      // await btn.trigger('click')
      // expect(spy).toHaveBeenCalled()
    })
  })
})
