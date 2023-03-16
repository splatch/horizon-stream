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
    <div
      class="severity-filter"
      data-test="severity-filter"
    >
      severity filter
    </div>
    <div class="content">
      <div class="sort-search">
        <div data-test="sort-date">sort by date</div>
        <div class="search-filter">
          <FeatherInput
            v-model="searchAlerts"
            @update:model-value="searchAlertsListener"
            label="Search Alerts"
            type="search"
            class="search-alerts-input"
            data-test="search-filter"
          />
        </div>
      </div>
      <AlertsCardList data-test="card-list" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAlertsStore } from '@/store/Views/alertsStore'

const alertsStore = useAlertsStore()

onMounted(async () => {
  await alertsStore.fetchAlerts()
})

const searchAlerts = ref('')
const searchAlertsListener = (v: any) => {
  // need to define (with BE dev), when to send request, how many chars,...
}
</script>

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
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: var(variables.$spacing-l);
}

.search-filter {
  width: 30%;
  .search-alerts-input {
    width: 100%;
  }
}

.content {
  background: white;
  padding: var(variables.$spacing-l);
}
</style>
