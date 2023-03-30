import { flowsAppDataToChartJS } from '@/dtos/chartJS.dto'
import { ChartData } from 'chart.js'
import { format } from 'date-fns'
import { defineStore } from 'pinia'
import { useflowsQueries } from '@/store/Queries/flowsQueries'

const flowsQueries = useflowsQueries()
export const useFlowsStore = defineStore('flowsStore', {
  state: () => ({
    tableDatasets: [{} as any],
    lineDatasets: [{} as any],
    tableChartOptions: {},
    totalFlows: '1,957',
    filters: {
      dateFilter: 'today',
      traffic: {
        selectedItem: 'total'
      },
      dataStyle: {
        selectedItem: 'table'
      }
    },
    applications: {
      isLoading: false,
      tableChartData: {} as ChartData,
      lineChartData: {} as ChartData,
      expansionOpen: true,
      filterDialogOpen: false,
      dialogFilters: { ...defaultDialogFilters }
    },
    exporters: {
      tableChartData: {} as ChartData,
      lineChartData: {} as ChartData,
      expansionOpen: true,
      filterDialogOpen: false,
      dialogFilters: { ...defaultDialogFilters }
    }
  }),
  actions: {
    getDatasets() {
      //Will be replaced with a BE call
      const returnedTableDataSets = [
        {
          label: 'app_0',
          bytesIn: 407371,
          bytesOut: 402374
        },
        {
          label: 'app_1',
          bytesIn: 63491,
          bytesOut: 62750
        },
        {
          label: 'app_2',
          bytesIn: 95187,
          bytesOut: 19754
        },
        {
          label: 'app_3',
          bytesIn: 76824,
          bytesOut: 95025
        },
        {
          label: 'app_4',
          bytesIn: 16870,
          bytesOut: 97879
        },
        {
          label: 'app_5',
          bytesIn: 86697,
          bytesOut: 90904
        },
        {
          label: 'app_6',
          bytesIn: 10761,
          bytesOut: 71282
        },
        {
          label: 'app_7',
          bytesIn: 67521,
          bytesOut: 86472
        },
        {
          label: 'app_8',
          bytesIn: 37723,
          bytesOut: 61793
        },
        {
          label: 'app_9',
          bytesIn: 36712,
          bytesOut: 63233
        }
      ]
      this.tableDatasets = returnedTableDataSets

      //Get Appliances
      const { data: applicationsLineData } = flowsQueries.getApplicationsSeries()
      const { data: applicationsTableData } = flowsQueries.getApplicationsSummaries()
      const { data: applicationsData } = flowsQueries.getApplications()

      console.log('LINE DATA ' + applicationsLineData.value)
      console.log('TABLE DATA ' + applicationsTableData.value)
      console.log('APPLICATIONS ' + applicationsData.value)

      // this.lineDatasets = flowsAppDataToChartJS(data.value)
    },
    createTableChartData() {
      //Dummy Data until BE is hooked up.
      this.exporters.tableChartData = {
        labels: this.tableDatasets.map((row) => row.label),
        datasets: [
          {
            label: 'Inbound',
            data: this.tableDatasets.map((row) => row.bytesIn),
            barThickness: 13,
            backgroundColor: '#0043A4',
            hidden: this.filters.traffic.selectedItem === 'outbound'
          },
          {
            label: 'Outbound',
            data: this.tableDatasets.map((row) => row.bytesOut),
            barThickness: 13,
            backgroundColor: '#EE7D00',
            hidden: this.filters.traffic.selectedItem === 'inbound'
          }
        ]
      }
      this.applications.tableChartData = {
        labels: this.tableDatasets.map((row) => row.label),
        datasets: [
          {
            label: 'Inbound',
            data: this.tableDatasets.map((row) => row.bytesIn),
            barThickness: 13,
            backgroundColor: '#0043A4',
            hidden: this.filters.traffic.selectedItem === 'outbound'
          },
          {
            label: 'Outbound',
            data: this.tableDatasets.map((row) => row.bytesOut),
            barThickness: 13,
            backgroundColor: '#EE7D00',
            hidden: this.filters.traffic.selectedItem === 'inbound'
          }
        ]
      }
    },
    createLineChartData() {
      const datasetArr = {
        type: 'line',
        datasets: this.lineDatasets.map((element) => {
          return {
            label: element.label,
            data: element.data.map((data: any) => {
              return {
                x: this.convertToDate(data.timestamp),
                y: data.value
              }
            }),
            fill: true
          }
        })
      }
      this.applications.lineChartData = datasetArr
      this.exporters.lineChartData = datasetArr
    },
    filterDialogToggle(event: Event, isAppFilter: boolean) {
      isAppFilter
        ? (this.applications.filterDialogOpen = !this.applications.filterDialogOpen)
        : (this.exporters.filterDialogOpen = !this.exporters.filterDialogOpen)
    },
    generateTableChart() {
      this.getDatasets()
      this.createTableChartData()
    },
    generateLineChart() {
      this.getDatasets()
      this.createLineChartData()
    },
    appDialogRefreshClick(e: Event) {
      const selectedFilters = this.getTrueValuesFromObject(this.applications.dialogFilters)
      console.log('you have selected ' + selectedFilters)
      this.filterDialogToggle(e, true)
    },
    expDialogRefreshClick(e: Event) {
      const selectedFilters = this.getTrueValuesFromObject(this.exporters.dialogFilters)
      console.log('you have selected ' + selectedFilters)
      this.filterDialogToggle(e, false)
    },
    getTrueValuesFromObject(object: object) {
      const keys = Object.keys(object)
      const filtered = keys.filter(function (key: string) {
        return object[key as keyof typeof object]
      })
      return filtered
    },
    trafficRadioOnChange(selectedItem: string) {
      this.filters.traffic.selectedItem = selectedItem
      this.generateTableChart()
    },
    convertToDate(ts: string) {
      const dateFormat = () => {
        switch (this.filters.dateFilter) {
          case 'today':
            return 'HH:mm'
          case '24h':
            return 'HH:mm'
          case '7d':
            return 'dd/MMM HH:mm'
          default:
            return 'dd/MMM HH:mm'
        }
      }
      return format(new Date(ts), dateFormat())
    },
    onDateFilterUpdate(e: any) {
      this.filters.dateFilter = e
      this.createLineChartData()
      this.createTableChartData()
    },
    getTimeRange(range: string) {
      const now = Date.now()
      let startTime
      switch (range) {
        case 'today':
          startTime = new Date(new Date().setHours(0, 0, 0, 0)).getTime()
          return { startTime: startTime, endTime: now }
        case '24h':
          startTime = now - 24 * 60 * 60 * 1000
          return { startTime: startTime, endTime: now }
        case '7d':
          startTime = now - 7 * 24 * 60 * 60 * 1000
          return { startTime: startTime, endTime: now }
        default:
          startTime = new Date(new Date().setHours(0, 0, 0, 0)).getTime()
          return { startTime: startTime, endTime: now }
      }
    }
  }
})

type FlowsDialogFilters = {
  http: boolean
  https: boolean
  pandoPub: boolean
  snmp: boolean
  imaps: boolean
}

const defaultDialogFilters: FlowsDialogFilters = {
  http: false,
  https: false,
  pandoPub: false,
  snmp: false,
  imaps: false
}
