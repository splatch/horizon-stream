import Graphs from '@/containers/graphs.vue'
import setupWrapper from 'tests/setupWrapper'

const wrapper = setupWrapper({
  component: Graphs
})

test('The Graphs container mounts correctly', () => {
  expect(wrapper).toBeTruthy()
})
