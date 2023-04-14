<template>
  <!--<DashboardEmptyState :texts="dashboardText.NetworkTraffic">
    <template v-slot:icon>
      <FeatherIcon
        :icon="isDark ? AreaChartDark : AreaChart"
        class="empty-chart-icon"
      />
    </template>
  </DashboardEmptyState>-->
  <Line
    :data="data"
    :options="config.options"
  />
</template>

<script setup lang="ts">
import dashboardText from '@/components/Dashboard/dashboard.text'
import AreaChart from '@/assets/AreaChart.svg'
import AreaChartDark from '@/assets/AreaChart-dark.svg'
import useTheme from '@/composables/useTheme'
import { Line } from 'vue-chartjs'
import { TimeRangeUnit } from '@/types/graphql'
import { format, eachHourOfInterval } from 'date-fns'
import { sortBy } from 'lodash'
const { isDark } = useTheme()

const params = {
  metrics: ['response_time_msec'],
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const values = [
  [1681481752.95, 31009702990],
  [1681481812.753, 31009959955],
  [1681481872.751, 31009998930],
  [1681481932.756, 31010196542],
  [1681481992.806, 31016311966],
  [1681482053.567, 31016768277],
  [1681482112.81, 31014066565],
  [1681482172.809, 31011798368],
  [1681482232.719, 31012377402],
  [1681482292.722, 31013490757],
  [1681482352.769, 31014043317],
  [1681482412.772, 31036284530],
  [1681482472.726, 31042176732],
  [1681482532.729, 31042290039],
  [1681482592.733, 31043342671],
  [1681482652.728, 31043541323],
  [1681482712.73, 31045497763],
  [1681482352.769, 31034043417],
  [1681482832.687, 31045806765],
  [1681482892.782, 31045949824],
  [1681483122.014, 31047157235],
  [1681483252.984, 31047924486],
  [1681483253.376, 31044590926],
  [1681483313.758, 31039396534],
  [1681482352.769, 31014043317]
]

const valuesFormatted = values.map((i) => {
  const transformToDate = (val: number) => parseInt(val.toString().replace('.', ''))
  const transformtoGb = (val: number) => val / 1e9
  return [transformToDate(i[0]), transformtoGb(i[1])]
})

const interval = eachHourOfInterval({
  start: new Date(Date.now() - 3600 * 24 * 1000),
  end: Date.now()
})
const labels = interval.map((i) => format(i, 'h a'))
const data = {
  labels: labels,
  datasets: [
    {
      data: valuesFormatted,
      segment: {
        borderColor: 'transparent'
      },
      label: 'Outbound'
    }
  ]
}

const config = {
  options: {
    fill: true,
    radius: 0,
    line: false,
    responsive: true,
    interaction: {
      mode: 'index',
      intersect: false
    },
    plugins: {
      title: {
        display: false
      },
      legend: {
        align: 'center',
        position: 'bottom',
        labels: {
          boxWidth: 18,
          boxHeight: 18,
          borderRadius: 20,
          useBorderRadius: true,
          padding: 20,
          font: {
            size: 15
          },
          usePointStyle: true
        }
      },
      tooltip: {
        usePointStyle: true,
        callbacks: {
          label: (context) => {
            return ` ${context.label} - ${context.parsed.y.toFixed(1)} Gb`
          },
          title: () => {
            return null
          },
          labelPointStyle: () => {
            return {
              pointStyle: 'circle',
              rotation: 0
            }
          }
        }
      }
    },
    scales: {
      x: {
        grid: {
          display: false
        },
        ticks: {
          callback: function (val, index) {
            return index % 2 === 0 ? this.getLabelForValue(val) : ''
          },
          maxRotation: 0,
          font: {
            size: 10
          }
        }
      },
      y: {
        type: 'linear',
        display: true,
        position: 'right',
        border: {
          display: true
        },
        grid: {
          display: false
        },
        ticks: {
          callback: function (val) {
            return val + ' Gb'
          }
        }
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
