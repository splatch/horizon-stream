<template>
  <PageHeader :heading="heading" />
  <!-- tabs anchor-->
  <!-- expandable filter: search, select, sorting -->
  <!-- card: title, metric chip list, text anchor list, icon action list -->
  <IconActionList />
</template>

<script lang="ts" setup>
const heading = 'Network Inventory'
</script>

<style lang="scss" scoped>
</style>