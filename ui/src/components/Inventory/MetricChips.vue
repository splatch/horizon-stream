<template>
  <FeatherChipList label="List of metric chips">
    <div v-for="item in items" :key="item.type" class="container">
      <MetricChip :item="item" />
    </div>
  </FeatherChipList>
</template>

<script lang="ts" setup>
import { FeatherChipList } from '@featherds/chips'
import { TimeUnit } from '@/types'
import { Chip } from '@/types/metric'
import MetricChip from './MetricChip.vue'

const items: Chip[] = [
  {
    type: 'latency',
    timestamp: -1667930274660,
    timeUnit: TimeUnit.MSecs,
    status: 'UP'
  },
  {
    type: 'uptime',
    timestamp: 1667930274.660,
    timeUnit: TimeUnit.Secs,
    status: 'DOWN'
  },
  {
    type: 'status',
    status: 'DOWN'
  }
]
</script>

<style lang="scss" scoped>
.container {
  text-align: left;
}
</style>