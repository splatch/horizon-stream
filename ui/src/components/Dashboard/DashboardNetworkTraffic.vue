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
import { format, fromUnixTime, isSameDay, formatISO } from 'date-fns'
const { isDark } = useTheme()

const params = {
  metrics: ['response_time_msec'],
  timeRange: 10,
  timeRangeUnit: TimeRangeUnit.Minute
}
const values = [
  [1681649682, 639440546.6416215],
  [1681653282, 508919101959.2322],
  [1681656882, 68278883114.71479],
  [1681660482, 734307700620.8582],
  [1681664082, 583662416203.6449],
  [1681667682, 521208602012.51465],
  [1681671282, 156552992309.7778],
  [1681674882, 608352551156.4628],
  [1681678482, 926031281879.0571],
  [1681682082, 45749331345.70422],
  [1681685682, 559170850545.3351],
  [1681689282, 5789705845.894407],
  [1681692882, 22229237435.0039482],
  [1681696482, 40773554023.95905],
  [1681700082, 1116630287842.7036],
  [1681703682, 474413966478.28345],
  [1681707282, 708300654466.2225],
  [1681710882, 321187578329.12506],
  [1681714482, 404563613803.0508],
  [1681718082, 88036336202.1175],
  [1681721682, 933772646320.0774],
  [1681725282, 393296129.3433485],
  [1681728882, 1242350636402.834],
  [1681732482, 345630601.32130504],
  [1681736082, 1423696281532.7114]
]

const values2 = [
  [1681649682, 963944084121.15],
  [1681653282, 202914601952.2322],
  [1681656882, 682783114.71479],
  [1681660482, 73430700620.8582],
  [1681664082, 58366416203.6449],
  [1681667682, 52128602012.51465],
  [1681671282, 15655992309.7778],
  [1681674882, 60835551156.4628],
  [1681678482, 92603281879.0571],
  [1681682082, 45749313.7088422],
  [1681685682, 5591708545.3351],
  [1681689282, 527897084.8944407],
  [1681692882, 22923735.0039482],
  [1681696482, 45540773540.06905],
  [1681700082, 11166387842.7036],
  [1681703682, 47441366478.28345],
  [1681707282, 70830054466.2225],
  [1681710882, 32118778329.12506],
  [1681714482, 40456313803.0508],
  [1681718082, 8803636202.1175],
  [1681721682, 93377646320.0774],
  [1681725282, 39329129.3433485],
  [1681728882, 124235066402.834],
  [1681732482, 34563001.32130504],
  [1681736082, 142369281532.7114]
]
const valuesFormatted = (list) =>
  list.map((i) => {
    const transformToDate = (val: number) => format(fromUnixTime(new Date(val)), 'h a')
    const transformtoGb = (val: number) => val / 1e9
    return [transformToDate(i[0]), transformtoGb(i[1])]
  })

const data = {
  labels: valuesFormatted(values).map((i) => i[0]),
  datasets: [
    {
      data: valuesFormatted(values),
      segment: {
        borderColor: 'transparent'
      },
      label: 'Outbound'
    },
    {
      data: valuesFormatted(values2),
      segment: {
        borderColor: 'transparent'
      },
      label: 'Inbound'
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
            const lastIndex = values.length - 1
            if (index == 0 || index == lastIndex) {
              const date = format(fromUnixTime(new Date(values[index][0])), 'LLL d')
              return this.getLabelForValue(date)
            }
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
