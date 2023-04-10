<template>
  <div class="flows">
    <BasicChart
      :id="'pieChartApplications'"
      :chart-options="constGraph"
      :chart-data="data"
      :chart-type="'polarArea'"
    >
    </BasicChart>
  </div>
</template>

<script setup lang="ts">
import { map, sum, sortBy } from 'lodash'
import { useFlowsStore } from '@/store/Views/flowsStore'
import useTheme from '@/composables/useTheme'
import { useMediaQuery } from '@vueuse/core'
const isLargeScreen = useMediaQuery('(min-width: 1024px)')
const { onThemeChange, isDark } = useTheme()
const flowsStore = useFlowsStore()
const router = useRouter()
const constGraph = ref()

//const isDark = useDark()
const mockApp = {
  data: {
    findApplicationSeries: [
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'at-rtmp',
        value: 28490,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'ssh',
        value: 29957,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'isakmp',
        value: 33683,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'ipsec-nat-t-application',
        value: 25578,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'ftp',
        value: 3972,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'https',
        value: 28439,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'imaps',
        value: 27037,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'snmptrap',
        value: 24627,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'bootpc',
        value: 27831,
        direction: 'INGRESS'
      },
      {
        timestamp: '2023-04-03T17:36:53Z',
        label: 'pop3',
        value: 14558,
        direction: 'INGRESS'
      }
    ]
  }
}

const values = map(sortBy(mockApp.data.findApplicationSeries, ['value']).reverse(), 'value')
const total = sum(values)
const percentages = map(values, (i) => (i * 100) / total)
const labels = map(
  mockApp.data.findApplicationSeries,
  (app, i) => `${i + 1}. ${app.label} (${percentages[i].toFixed(2)}%)`
)
const data = {
  labels: labels,
  datasets: [
    {
      data: percentages
    }
  ]
}

const titleLargeScreenConfig = {
  align: 'start',
  padding: 20,
  font: { size: 20 }
}

const titleSmallScreenConfig = {
  align: 'center',
  padding: {
    top: 10,
    bottom: 10
  },
  font: { size: 16 }
}

const config = {
  layout: {},
  responsive: true,
  plugins: {
    title: {
      ...{
        display: true,
        text: 'Top 10 applications',
        color: isDark.value ? '#d1d0d0' : '#00000'
      },
      ...(isLargeScreen.value ? titleLargeScreenConfig : titleSmallScreenConfig)
    },
    legend: {
      display: isLargeScreen.value,
      align: 'center',
      position: 'right',
      fullSize: true,
      maxWidth: 200,
      labels: {
        boxWidth: 22,
        boxHeight: 22,
        borderRadius: 20,
        useBorderRadius: true,
        padding: 20,
        color: isDark.value ? '#d1d0d0' : '#00000',
        font: {
          size: 15
        }
      }
    }
  }
}
constGraph.value = config

onMounted(async () => {
  await flowsStore.updateCharts()
})

const redirect = (route: string) => {
  router.push(route)
}

onThemeChange(() => {
  config.plugins.title.color = isDark.value ? '#d1d0d0' : '#00000'
  config.plugins.legend.labels.color = isDark.value ? '#d1d0d0' : '#00000'
  constGraph.value = { ...config }
})
</script>

<style scoped lang="scss">
@use '@featherds/styles/mixins/typography';
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';

.flows {
  width: 100%;
  background-color: var(variables.$surface);
  padding: 0 var(variables.$spacing-xl);
  border: 1px solid var(variables.$border-on-surface);
  @include mediaQueriesMixins.screen-md {
    width: 50%;
  }
}
.section-title {
  @include typography.headline3();
}
</style>
