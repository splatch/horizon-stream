<template>
  <div class="container">
      <div class="canvas-wrapper">
        <canvas :id="`${label}`"></canvas>
      </div>
  </div>
</template>
  
<script setup lang="ts">
import { useGraphs } from '@/composables/useGraphs'
import { ChartOptions, TitleOptions, ChartData } from 'chart.js'
import { Chart, registerables }  from 'chart.js'
// import zoomPlugin from 'chartjs-plugin-zoom'
import { PropType } from 'vue'
import { formatTimestamp } from './utils'
Chart.register(...registerables)
// Chart.register(zoomPlugin) disable zoom until phase 2

const graphs = useGraphs()

const props = defineProps({
  metricStrings: {
    required: true,
    type: Array as PropType<string[]>
  },
  label: {
    required: true,
    type: String
  }
})

let chart: any = {}

const options = computed<ChartOptions>(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    title: {
      display: true,
      text: props.label
    } as TitleOptions,
    zoom: {
      zoom: {
        wheel: {
          enabled: true
        },
        mode: 'x'
      },
      pan: {
        enabled: true,
        mode: 'x'
      }
    }
  },
  scales: {
    y: {
      title: {
        display: false,
        text: props.label
      } as TitleOptions,
      ticks: {
        maxTicksLimit: 8
      },
      stacked: false
    },
    x: {
      ticks: {
        maxTicksLimit: 12
      }
    }
  }
}))

const xAxisLabels = computed(() => {
  const totalLabels: any = new Set()

  graphs.dataSets.value[0].values.forEach(value => {
    if(value[0]) totalLabels.add(formatTimestamp(value[0], 'hours'))
  })

  // return totalLabels
  return ['01:12', '01:22', '01:32', '01:42']
})

const dataSets = computed(() => {
  const dataSets: any = []
  const dataObject = {
    label: graphs.dataSets.value[0].metric.__name__,
    data: graphs.dataSets.value[0].values.map((value) => {
      if (value[1]) return value[1]
    }),
    /* data: [
      '10.069208',
      '8.355875',
      '6.045458',
      '31.727167'
    ], */
    backgroundColor: 'green'
  }

  dataSets.push(dataObject)
  
  return dataSets
})

const chartData = computed<ChartData<any>>(() => {
  return {
    labels: xAxisLabels.value,
    datasets: dataSets.value
  }
})

const render = async (update?: boolean) => {
  try {
    if (update) {
      chart.data = chartData.value
      chart.update()
    } else {
      const ctx: any = document.getElementById(`${props.label}`)
      chart = new Chart(ctx, {
        type: 'line',
        data: chartData.value,
        options: options.value,
        plugins: []
      })
    }
  } catch (error) {
    console.log(error)
    console.log('Could not render graph for ', props.label)
  }
}

onMounted(async () => {
  await graphs.getMetrics(props.metricStrings)
  render()
})
</script>
  
<style scoped lang="scss">
.container {
  position: relative;
}
.canvas-wrapper {
  display: block;
  height: 300px;
}
</style>
