<template>
  <div class="feather-row">
    <div class="feather-col-12 container">
        <div class="canvas-wrapper">
          <canvas :id="`${label}`"></canvas>
        </div>
    </div>
  </div>
</template>
  
<script setup lang="ts">
import { ChartOptions, TitleOptions, ChartData } from 'chart.js'
import { Chart, registerables }  from 'chart.js'
import zoomPlugin from 'chartjs-plugin-zoom'
import { PropType } from 'vue'
import { DataSets } from '@/types/graphs'
import { formatTimestamp } from './utils'
import { uniq } from 'lodash'
Chart.register(...registerables)
Chart.register(zoomPlugin)

const props = defineProps({
  dataSets: {
    required: true,
    type: Array as PropType<DataSets>
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
        display: true,
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
  const totalLabels = []

  for (const dataSet of props.dataSets) {
    const labels = dataSet.map((result) => {
      if (result.value) return formatTimestamp(result.value[0], 'hours')
    })
    totalLabels.push(...labels)
  }

  return uniq(totalLabels)
})

const dataSets = computed(() => {
  const dataSets: any = []

  for (const dataSet of props.dataSets) {
    const dataObject = {
      label: dataSet[0].metric.__name__,
      data: dataSet.map((result) => {
        if (result.value) return result.value[1]
      }),
      backgroundColor: 'green'
    }

    dataSets.push(dataObject)
  }
  
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

onMounted(() => render())
</script>
  
<style scoped lang="scss">
.container {
  position: relative;
}
.canvas-wrapper {
  display: block;
  height: 370px;
}
</style>
