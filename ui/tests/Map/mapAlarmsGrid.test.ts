import { mount } from '@vue/test-utils'
import MapAlarmsGrid from '@/components/Map/MapAlarmsGrid.vue'
import { createTestingPinia } from '@pinia/testing'
import { createClient, VILLUS_CLIENT } from 'villus'

let wrapper: any

beforeEach(() => {
  wrapper= mount(MapAlarmsGrid, {
    global: {
      plugins: [ createTestingPinia() ],
      provide: {
        [VILLUS_CLIENT as unknown as string]: createClient({
          url: 'https://test/graphql'
        })
      }
    }
  })
})

const columns = [
  [ 'ID', 'col-id' ],
  [ 'SEVERITY', 'col-severity' ],
  [ 'NODE LABEL', 'col-node-label' ],
  [ 'UEI', 'col-uei' ],
  [ 'COUNT', 'col-count' ],
  [ 'LAST EVENT', 'col-last-event' ],
  [ 'LOG MESSAGE', 'col-log-msg' ]
]
it.each(columns)('should have %s column', (_, col) => {
  const elem = wrapper.get(`[data-test="${col}"]`)
  expect(elem.exists()).toBeTruthy()
})

it('should have a dropdown selection input', () => {
  const elem = wrapper.get('[data-test="select-ack"]')
  expect(elem.exists()).toBeTruthy()
})

describe('selectAlarmAck method', () => {
  // todo: when alarm list data avail, assert the modifyAlarm() has been called ()
  test.skip('...', () => {
    expect(true).toBeTruthy()
  })
})
