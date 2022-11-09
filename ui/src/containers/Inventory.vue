<template>
  <PageHeader :heading="heading" />
  <!-- tabs anchor-->
  <!-- expandable filter: search, select, sorting -->
  <!-- card: title, metric chip, list anchor, actions -->
  <AnchorList :anchor="anchor" />
  <div/>
</template>

<script lang="ts" setup>
const heading = 'Network Inventory'

const anchor = {
  profileLabel: 'Monitoring Profile',
  profileValue: 75,
  profileLink: '#',
  locationLabel: 'Monitoring Location',
  locationValue: 'DefaultMinion',
  locationLink: '#',
  ipInterfaceLabel: 'IP Interface',
  ipInterfaceValue: 25,
  ipInterfaceLink: '#',
  tagLabel: 'Tag',
  tagValue: 100,
  tagLink: '#'
}
</script>

<style lang="scss" scoped>
</style>