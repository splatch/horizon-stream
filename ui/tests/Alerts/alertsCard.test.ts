import { mount } from '@vue/test-utils'
import AlertsCard from '@/components/Alerts/AlertsCard.vue'

const mock = {
  id: 1,
  name: 'alert1',
  severity: 'Critical',
  cause: 'Power supply failure',
  duration: '3hrs',
  node: 'Server',
  date: '99-99-9999',
  time: '00:00:00',
  isAcknowledged: true,
  description: 'Sit lorem kasd diam....'
}

let wrapper: any

describe('Alert card', () => {
  beforeAll(() => {
    wrapper = mount(AlertsCard, {
      shallow: true,
      props: {
        alert: mock
      }
    })
  })

  test('Mount', () => {
    expect(wrapper).toBeTruthy()
  })

  test('Should have required elements', () => {
    const elems = ['checkbox', 'name', 'node-type', 'severity', 'cause', 'duration', 'date', 'time', 'check-icon']
    elems.forEach((elem) => {
      const el = wrapper.get(`[data-test="${elem}"]`)
      expect(el.exists()).toBeTruthy()
    })
  })
})
