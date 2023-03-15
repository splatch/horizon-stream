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
    <AlertsSeverityFilters />
    <div
      class="severity-filter"
      data-test="severity-filter"
    ></div>
    <div class="content">
      <div class="sort-search">
        <div data-test="sort-date">sort by date</div>
        <div data-test="search-filter">search filter</div>
      </div>
      <AlertsCardList data-test="card-list" />
    </div>
  </div>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.container {
  min-width: 1100px;
  margin-right: var(variables.$spacing-l);
  margin-left: var(variables.$spacing-l);
}

.header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.sort-search {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: var(variables.$spacing-l);
}

.content {
  background: white;
  padding: var(variables.$spacing-l);
}
</style>
