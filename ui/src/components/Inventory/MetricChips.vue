<template>
  <FeatherChipList label="List of test chips, one is disabled">
    <div v-for="item in items" :key="item.label" class="container">
      <label :for="item.label">{{ item.label }}</label>
      <FeatherChip :id="item.label">
        {{ item.timeUnit ? getHumanReadableDuration(item.value, item.timeUnit) : item.value }}
      </FeatherChip>
    </div>
  </FeatherChipList>
</template>
<script lang="ts" setup>
import { FeatherChipList, FeatherChip } from '@featherds/chips'
import { TimeUnit } from '@/types'
import { getHumanReadableDuration } from '@/components/utils'

const items = [
  {
    label: 'latency',
    value: 945,
    bgColor: 'red',
    timeUnit: TimeUnit.Secs
  },
  {
    label: 'uptime',
    value: 58822,
    bgColor: 'yellow',
    timeUnit: TimeUnit.MSecs
  },
  {
    label: 'status',
    value: 'DOWN',
    bgColor: 'green'
  }
]
</script>
<style lang="scss" scoped>
label {
  display: block;
}
.container {
  text-align: left;
}

</style>