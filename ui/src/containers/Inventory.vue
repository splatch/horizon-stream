<template>
  <PageHeader :heading="heading" />
  <!-- tabs anchor-->
  <!-- expandable filter: search, select, sorting -->
  <!-- card: title, metric pill, list anchor, actions -->
  <MetricChips />
  <div/>
</template>
<script lang="ts" setup>
import PageHeader from '@/components/Common/PageHeader.vue'
import MetricChips from '@/components/Inventory/MetricChips.vue'

const heading = 'Network Inventory'
</script>
<style lang="scss" scoped>
</style>