import GridTabs from '@/components/Map/GridTabs.vue'
import useRouter from '@/composables/useRouter'
import setupWrapper from '../helpers/setupWrapper'

let wrapper: any

beforeEach(() => {
  wrapper = setupWrapper(GridTabs)
})

it('should redirect to the alarm list', async () => {
  const router = useRouter()
  const push = vi.spyOn(router, 'push')

  await wrapper.get('[data-test="alarm-tab"]').trigger('click')

  expect(push).toHaveBeenCalledTimes(1)
  expect(push).toHaveBeenCalledWith({path: '/map'})
}),

it('should redirect to the alarm of a node', async () => {
  const router = useRouter()
  const push = vi.spyOn(router, 'push')

  const route = useRoute()
  route.query.nodeId = '1234'

  await wrapper.get('[data-test="alarm-tab"]').trigger('click')

  expect(push).toHaveBeenCalledTimes(1)
  expect(push).toHaveBeenCalledWith({path: '/map?nodeId=1234'})
})

it('should redirect to the node list', async () => {
  const router = useRouter()
  const push = vi.spyOn(router, 'push')

  await wrapper.get('[data-test="nodes-tab"]').trigger('click')

  expect(push).toHaveBeenCalledTimes(1)
  expect(push).toHaveBeenCalledWith({path: '/map/nodes'})
})