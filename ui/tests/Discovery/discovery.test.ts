import Discovery from '@/containers/Discovery.vue'
import setupWrapper from 'tests/setupWrapper'

const wrapper = setupWrapper({ 
  component: Discovery 
})

test('The Discovery page container mounts correctly', () => {
  expect(wrapper).toBeTruthy()
})
