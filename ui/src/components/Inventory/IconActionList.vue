<template>
  <ul class="icon-action-list">
    <li :title="bubbleChartItem.title" class="pointer">
      <FeatherIcon @click="bubbleChartItem.onBubbleChart" :icon="bubbleChart" :title="bubbleChartItem.title" />
    </li>
    <li :title="lineChartItem.title" class="pointer">
      <FeatherIcon @click="lineChartItem.onLineChart" :icon="multilineChart" :title="lineChartItem.title" data-test="line-chart-icon" />
    </li>
    <li :title="pieChartItem.title" class="pointer">
      <FeatherIcon @click="pieChartItem.onPieChart" :icon="pieChart" :title="pieChartItem.title" />
    </li>
    <li :title="warningItem.title" class="pointer">
      <FeatherIcon @click="warningItem.onWarning" :icon="warningItem.icon" :title="warningItem.title" />
    </li>
    <li :title="deleteItem.title" class="pointer">
      <FeatherIcon @click="deleteItem.onDelete" :icon="deleteItem.icon" :title="deleteItem.title" />
    </li>
  </ul>
</template>

<script lang="ts" setup>
import bubbleChart from '@material-design-icons/svg/outlined/bubble_chart.svg'
import multilineChart from '@material-design-icons/svg/outlined/multiline_chart.svg'
import pieChart from '@material-design-icons/svg/outlined/pie_chart.svg'
import Warning from '@featherds/icon/notification/Warning'
import Delete from '@featherds/icon/action/Delete'
import { markRaw } from 'vue'

const bubbleChartItem = {
  title: 'Bubble Chart',
  onBubbleChart: () => {
    console.log('bubble chart')
  }
}
const lineChartItem = {
  title: 'Line Chart',
  onLineChart: () => {
    console.log('line chart')
  }
}
const pieChartItem = {
  title: 'Pie Chart',
  onPieChart: () => {
    console.log('pie chart')
  }
}
const warningItem = {
  icon: markRaw(Warning),
  title: 'Warning',
  onWarning: () => {
    console.log('warning')
  }
}
const deleteItem = {
  icon: markRaw(Delete),
  title: 'Delete',
  onDelete: () => {
    console.log('delete')
  }
}
</script>

<style lang="scss" scoped>
@use "@featherds/styles/themes/variables";

.icon-action-list {
  list-style-type: none;
  padding: 0;
  display: inline-block;
  flex-direction: column;
}
.feather-icon {
  font-size: 1.5rem;
  color: var(variables.$primary);
}
</style>