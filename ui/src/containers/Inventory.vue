<template>
  <PageHeader :heading="heading" />
  <!-- tabs anchor-->
  <!-- expandable filter: search, select, sorting -->
  <!-- card: title, metric chip, list anchor, actions -->
  <AnchorList :anchors="anchors" />
  <div/>
</template>

<script lang="ts" setup>
const heading = 'Network Inventory'

const anchors = [
  {
    label: 'Monitoring Profile',
    value: 75,
    link: '#'
  },
  {
    label: 'Monitoring Location',
    value: 'DefaultMinion',
    link: '#'
  },
  {
    label: 'IP Interface',
    value: 25,
    link: '#'
  },
  {
    label: 'Tag',
    value: 100,
    link: '#'
  }
]
</script>

<style lang="scss" scoped>
</style>