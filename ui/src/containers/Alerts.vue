<template>
  <div class="container">
    <div class="header">
      <PageHeadline
        text="Alerts"
        data-test="headline"
      />
      <FeatherButton
        secondary
        class="clear-all-filters-btn"
        data-test="clear-all-filters-btn"
        >clear all filters</FeatherButton
      >
    </div>
    <!-- severity filter -->
    <div class="content">
      <div>
        <!-- sort by date-->
        <!-- search filter -->
      </div>
      <ACardList data-test="card-list" />
    </div>
  </div>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.container {
  min-width: 1130px;
  margin-right: var(variables.$spacing-l);
  margin-left: var(variables.$spacing-l);
}

.header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.content {
  background: white;
  padding: var(variables.$spacing-l) var(variables.$spacing-xs);
}
</style>
