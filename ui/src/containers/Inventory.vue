<template>
  <PageHeader :heading="heading" />
  <!-- tabs anchor-->
  <!-- expandable filter: search, select, sorting -->
  <!-- card: title, metric chip list, text anchor list, icon action list -->
  <MetricChipList />
  <div/>
</template>

<script lang="ts" setup>
import MetricChipList from '@/components/Inventory/MetricChipList.vue'

const heading = 'Network Inventory'
</script>

<style lang="scss" scoped>
</style>