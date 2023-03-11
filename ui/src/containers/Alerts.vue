<template>
  <div class="container">
    <div class="header">
      <PageHeadline
        text="Alerts"
        data-test="headline"
      />
      <FeatherButton
        secondary
        data-test="clear-all-filters-btn"
        >clear all filters</FeatherButton
      >
    </div>
    <!-- severity filter -->
    <div>
      <!-- sort by date-->
      <!-- search filter -->
    </div>
    <ACardList data-test="card-list" />
  </div>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.container {
  margin: 0 var(variables.$spacing-l);
}
</style>
