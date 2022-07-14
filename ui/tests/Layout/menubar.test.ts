import Menubar from '@/components/Layout/Menubar.vue'
import { createTestingPinia } from '@pinia/testing'
import { mount } from '@vue/test-utils'

test('The menubar mounts', () => {
  const wrapper = mount(Menubar, { global: { plugins: [createTestingPinia()] }, props: {} })
  expect(wrapper).toBeTruthy()
})
