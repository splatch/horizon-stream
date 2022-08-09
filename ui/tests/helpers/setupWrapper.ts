import { mount, shallowMount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import { VILLUS_CLIENT, createClient } from 'villus'

interface MountingOptions {
  stubs?: Record<string, any>,
  plugins?: [...any[]],
  provide?: Record<string, any>
}

const wrapper = (component: any, mountingType = 'mount', global: MountingOptions = {}): any => {
  const { stubs, plugins, provide } = global
  const globalOptions: Record<string, any> = {
    global: {
      stubs: { ...stubs },
      plugins: [ 
        createTestingPinia(),
        plugins 
      ],
      provide: {
        [VILLUS_CLIENT as unknown as string]: createClient({
          url: 'https://test/graphql'
        }),
        ...provide
      }
    }
  }
  
  if(mountingType === 'mount') return mount(component, globalOptions)

  return shallowMount(component, globalOptions)
}

export default wrapper