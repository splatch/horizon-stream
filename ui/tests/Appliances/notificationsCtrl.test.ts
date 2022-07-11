import Appliances from '@/containers/Appliances.vue'
import { createTestingPinia } from '@pinia/testing'
import { mount } from '@vue/test-utils'
import { createClient, VILLUS_CLIENT } from 'villus'

const wrapper = mount(Appliances, { 
  global: { 
    plugins: [createTestingPinia()],
    provide: {
      [VILLUS_CLIENT as unknown as string]: createClient({
        url: 'http://test/graphql',
      })
    }
  }
})

test('The component mounts', () => {
  expect(wrapper).toBeTruthy()
})
