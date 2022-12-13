<template>
  <div class="container">
      <div class="canvas-wrapper">

        <FeatherTooltip
          title="Download to PDF"
          v-slot="{ attrs, on }"
        >
          <FeatherButton
            v-bind="attrs" 
            v-on="on" 
            icon="Download" 
            class="download-icon" 
            @click="onDownload" 
            v-if="graphs.dataSets.value.length"
          >
            <FeatherIcon :icon="DownloadFile" />
          </FeatherButton>
        </FeatherTooltip>

        <canvas class="canvas" :id="`${graph.label}`"></canvas>
      </div>
  </div>
</template>
  
<script setup lang="ts">
import { useGraphs } from '@/composables/useGraphs'
import { ChartOptions, TitleOptions, ChartData } from 'chart.js'
import Chart from 'chart.js/auto'
// import zoomPlugin from 'chartjs-plugin-zoom'
import { PropType } from 'vue'
import { formatTimestamp, downloadCanvas } from './utils'
import { GraphProps } from '@/types/graphs'
import DownloadFile from '@featherds/icon/action/DownloadFile'
import { format } from 'd3'

// Chart.register(zoomPlugin) disable zoom until phase 2

const graphs = useGraphs()

const props = defineProps({
  graph: {
    required: true,
    type: Object as PropType<GraphProps>
  }
})

const yAxisFormatter = format('.3s')

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
        callback: (value) => yAxisFormatter(value as number),
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
    backgroundColor: bgColor[i],
    borderColor: bgColor[i],
    hitRadius: 5,
    hoverRadius: 6,
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

const onDownload = () => {
  const canvas = document.getElementById(props.graph.label) as HTMLCanvasElement
  downloadCanvas(canvas, props.graph.label)
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
  margin-right: 10px;
}
.canvas-wrapper {
  display: block;
  height: 300px;
  position: relative;

  .download-icon {
    position: absolute;
    right: 10px;
    top: 30px;

    svg {
      font-size: 15px;
    }
  }
}
</style>
