import Menubar from '@/components/Layout/Menubar.vue'
import { test, expect } from 'vitest'
import { mount } from '@vue/test-utils'

test('The menubar mounts', () => {
  const wrapper = mount(Menubar)
  expect(wrapper).toBeTruthy()
})
