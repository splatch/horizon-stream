<template>
  <FeatherChipList label="List of test chips, one is disabled">
    <div v-for="item in items" :key="item.type" class="container">
      <label :for="item.type">{{ item.type }}</label>
      <FeatherChip :id="item.type" :class="`bg-status ${item.status}`">
        {{ item.type === 'status' ? item.status : getHumanReadableDuration(item.timestamp as number, item.timeUnit) }}
      </FeatherChip>
    </div>
  </FeatherChipList>
</template>

<script lang="ts" setup>
import { FeatherChipList, FeatherChip } from '@featherds/chips'
import { TimeUnit } from '@/types'
import { getHumanReadableDuration } from '@/components/utils'

interface Chip {
type: string,
timestamp?: number,
timeUnit?: number,
status: string
}
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
@use "@/styles/_statusBackground";

label {
  display: block;
}
.container {
  text-align: left;
}
</style>