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
  const totalLabels = graphs.dataSets.value[0].values.map(([timestamp]) => {
    return formatTimestamp(timestamp * 1000, 'minutes') // TODO: '* 1000' may not be needed! if not then '/ 1000' must be removed from formatTimestamp method
  })

  return totalLabels
})

const dataSets = computed(() => {
  const graphsDataSets = graphs.dataSets.value[0]

  return [{
    label: graphsDataSets.metric.__name__,
    data: graphsDataSets.values.map(([, value]) => value),
    backgroundColor: 'green' // TODO: use featherds var
  }]
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
        type: 'line', // TODO: parameterize type
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

// TODO: make theme switching works in graphs
<style scoped lang="scss">
.container {
  position: relative;
}
.canvas-wrapper {
  display: block;
  height: 300px;
}
</style>
