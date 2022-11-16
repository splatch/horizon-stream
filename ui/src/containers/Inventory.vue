<template>
  <PageHeader :heading="heading" />
  <!-- tabs anchor-->
  <!-- expandable filter: search, select, sorting -->
  <!-- card: title, metric chip list, text anchor list, icon action list -->
  <TextAnchorList :anchor="anchor" />
</template>

<script lang="ts" setup>
const heading = 'Network Inventory'

const anchor = {
  profileValue: 75,
  profileLink: '#',
  locationValue: 'DefaultMinion',
  locationLink: '#',
  ipInterfaceValue: 25,
  ipInterfaceLink: '#',
  tagValue: 100,
  tagLink: '#'
}
</script>

<style lang="scss" scoped>
</style>