import Flows from '@/containers/Flows.vue'
import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import { FlowsState } from './flowsState'

describe('Flows', () => {
  let wrapper: any

  beforeAll(() => {
    wrapper = mount(Flows, {
      global: {
        plugins: [
          createTestingPinia({
            initialState: FlowsState
          })
        ]
      }
    })
  })

  test('The Flows page container mounts correctly', () => {
    expect(wrapper).toBeTruthy()
  })

  test('The Flows page title should be Flows', () => {
    const title = wrapper.get('[data-test="flows-page-header"]')
    expect(title.text()).toBe('Flows')
  })

  //TODO
  // FIX Error for (when using stub actions: false):
  // Cannot detect villus Client, did you forget to call `useClient`? Alternatively, you can explicitly pass a client as the `manualClient` argument.
  // test('The Flows Store should return a colour', () => {
  //   const store = useFlowsStore()
  //   const randomColour = store.randomColours(0)

  //   function isHex(num: string) {
  //     return Boolean(num.match(/^0x[0-9a-f]+$/i))
  //   }

  //   expect(isHex(randomColour)).toBeTruthy()
  // })
})
