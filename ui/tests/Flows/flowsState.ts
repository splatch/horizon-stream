import { TimeRange, RequestCriteriaInput } from '@/types/graphql'
import { IAutocompleteItemType } from '@featherds/autocomplete'
import { ChartData } from 'chart.js'

export const FlowsState = {
  tableDatasets: [{} as any],
  lineDatasets: [{} as any],
  tableChartOptions: {},
  totalFlows: '1,957',
  filters: {
    dateFilter: TimeRange.Today,
    traffic: {
      selectedItem: 'total'
    },
    dataStyle: {
      selectedItem: 'table'
    },
    //Application AutoComplete
    applications: [] as IAutocompleteItemType[],
    selectedApplications: [],
    isApplicationsLoading: false,
    filteredApplications: [] as IAutocompleteItemType[],
    //Exporter AutoComplete
    exporters: [] as IAutocompleteItemType[],
    selectedExporters: [],
    isExportersLoading: false,
    filteredExporters: [] as IAutocompleteItemType[]
  },
  applications: {
    isTableLoading: false,
    isLineLoading: false,
    tableChartData: {} as ChartData,
    lineChartData: {} as ChartData,
    expansionOpen: true,
    filterDialogOpen: false
  },
  exporters: {
    isLoading: false,
    tableChartData: {} as ChartData,
    lineChartData: {} as ChartData,
    expansionOpen: true,
    filterDialogOpen: false
  },
  requestCriteria: {
    count: 10,
    step: 2000000,
    timeRange: { startTime: 0, endTime: 0 },
    applications: [] as string[],
    exporters: []
  } as RequestCriteriaInput
}
