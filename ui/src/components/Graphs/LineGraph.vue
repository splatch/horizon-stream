<template>
  <div class="container">
      <div class="canvas-wrapper">
        <canvas :id="`${graph.label}`"></canvas>
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
import { GraphProps } from '@/types/graphs'

Chart.register(...registerables)
// Chart.register(zoomPlugin) disable zoom until phase 2

const graphs = useGraphs()

const props = defineProps({
  graph: {
    required: true,
    type: Object as PropType<GraphProps>
  }
})

let chart: any = {}

const options = computed<ChartOptions>(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    title: {
      display: true,
      text: props.graph.label
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
        text: props.graph.label
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
  const graphsDataSetsValues = graphs.dataSets.value[0].values as any
  
  const totalLabels = graphsDataSetsValues.map((val: any) => {
    return formatTimestamp(val[0], 'minutes')
  })

  return totalLabels
})

const dataSets = computed(() => {
  const bgColor = ['green', 'blue'] // TODO: better solution to set bg color in regards to FeatherDS theme switching
  return graphs.dataSets.value.map((data: any ,i) => ({
    label: data.metric.__name__,
    data: data.values.map((val: any) => val[1]),
    backgroundColor: bgColor[i]
  }))
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
      const ctx: any = document.getElementById(`${props.graph.label}`)
      chart = new Chart(ctx, {
        type: 'line',
        data: chartData.value,
        options: options.value,
        plugins: []
      })
    }
  } catch (error) {
    console.log(error)
    console.log('Could not render graph for ', props.graph.label)
  }
}

onMounted(async () => {
  await graphs.getMetrics(props.graph)
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
