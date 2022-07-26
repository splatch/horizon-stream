import { mount } from '@vue/test-utils'
import DeviceTable from '@/components/Appliances/DeviceTable.vue'
import deviceQueriesStore from '../store/deviceQueries'

describe('DeviceTable.vue', () => {
  describe('Required columns', () => {
    beforeAll(() => {
      const deviceItems = [
        {
          id: '1',
          name: 'device1',
          icmp_latency: 'OK',
          snmp_uptime: 'FAILED',
          status: 'UP'
        }
      ]
      deviceQueriesStore(computed(() => deviceItems)) 
    })

    const requiredColumns = [
      ['Device', 'col-device'],
      ['Latency', 'col-latency'],
      ['Uptime', 'col-uptime'],
      ['Status', 'col-status']
    ]

    it.each(requiredColumns)('should have "%s" column', (_, dataTest) => {
      const wrapper = mount(DeviceTable)
       
      const elem = wrapper.find(`[data-test="${dataTest}"]`)
      expect(elem.exists()).toBe(true)
    })
  })

  describe('Device list', () => {
    it('should have an empty table when there\'s no device', () =>{
      deviceQueriesStore(computed(() => [])) 
      const wrapper = mount(DeviceTable)
         
      const deviceItem = wrapper.find('[data-test="device-item"]')
      expect(deviceItem.exists()).toBe(false)
    })
    
    it('should display a list when there\'s device', () => {
      const deviceItems = [
        {
          id: '1',
          name: 'device1',
          icmp_latency: 'UNKNOWN',
          snmp_uptime: 'UNKNOWN',
          status: 'DOWN'
        }
      ]
      deviceQueriesStore(computed(() => deviceItems)) 
      const wrapper = mount(DeviceTable)

      const deviceItem = wrapper.find('[data-test="device-item"]')
      expect(deviceItem.exists()).toBe(true)
    })
  })

  describe('Background color coded', () => {
    describe('Latency/Uptime', () => {
      beforeAll(() => {
        const deviceItems = [
          {
            id: '1',
            name: 'device1',
            icmp_latency: 'OK',
            snmp_uptime: 'OK',
            status: 'UP'
          },
          {
            id: '2',
            name: 'device2',
            icmp_latency: 'FAILED',
            snmp_uptime: 'FAILED',
            status: 'DOWN'
          },
          {
            id: '3',
            name: 'device3',
            icmp_latency: 'UNKNOWN',
            snmp_uptime: 'UNKNOWN',
            status: 'DOWN'
          }
        ] 
        deviceQueriesStore(computed(() => deviceItems)) 
      })
  
      const formatValueBackground = (elems: any[]) => {
        return elems.map((elem: { classes: () => any; text: () => any }) => {
          const css = elem.classes().filter((cl: string | string[]) => cl.indexOf('bg-') >= 0)[0]
          const val = ['OK', 'FAILED', 'UNKNOWN'].filter((val: string) => elem.text().indexOf(val) >= 0)[0]
          return [ val, css]
        })
      }

      test('Latency OK/FAILED/KNOWN should have its corresponding background color', () => {
        const wrapper = mount(DeviceTable)

        const latencies = formatValueBackground(wrapper.findAll('[data-test="col-latency"]'))
        const expectedValueBackground = [
          ['OK', 'bg-ok'],
          ['FAILED', 'bg-failed'],
          ['UNKNOWN', 'bg-unknown']
        ]
        expect(latencies).toStrictEqual(expectedValueBackground)
      })
        
      test('Uptime OK/FAILED/KNOWN should have its corresponding background color', () => {
        const wrapper = mount(DeviceTable)

        const uptimes = formatValueBackground(wrapper.findAll('[data-test="col-uptime"]'))
        const expectedValueBackground = [
          ['OK', 'bg-ok'],
          ['FAILED', 'bg-failed'],
          ['UNKNOWN', 'bg-unknown']
        ]
        expect(uptimes).toStrictEqual(expectedValueBackground)
      })
    })
    
    describe('Status', () => {
      beforeAll(() => {
        const deviceItems = [
          {
            id: '1',
            name: 'device1',
            icmp_latency: 'OK',
            snmp_uptime: 'OK',
            status: 'UP'
          },
          {
            id: '2',
            name: 'device2',
            icmp_latency: 'FAILED',
            snmp_uptime: 'FAILED',
            status: 'DOWN'
          }
        ] 
        deviceQueriesStore(computed(() => deviceItems)) 
      })

      const formatValueBackground = (elems: any[]) => {
        return elems.map((elem: { classes: () => any; text: () => any }) => {
          const css = elem.classes().filter((cl: string | string[]) => cl.indexOf('bg-') >= 0)[0]
          const val = ['UP','DOWN'].filter((val: string) => elem.text().indexOf(val) >= 0)[0]
          return [ val, css]
        })
      }

      test('Status UP/DOWN should have its corresponding background color', () => {
        const wrapper = mount(DeviceTable)
        
        const statuses = formatValueBackground(wrapper.findAll('[data-test="col-status"]'))
        const expectedValueBackground = [
          ['UP', 'bg-ok'],
          ['DOWN', 'bg-failed']
        ]
        expect(statuses).toStrictEqual(expectedValueBackground)
      })
    })
  })
})