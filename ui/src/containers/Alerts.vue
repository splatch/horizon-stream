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
    <AlertsSeverityFilters data-test="severity-filter" />
    <div class="content">
      <div class="time-search-filter">
        <div
          class="time-filter"
          data-test="time-filter"
        >
          <span
            @click="selectTimeFilter(undefined)"
            :class="{ selected: timeFilterSelected === undefined }"
            >All</span
          >
          <span
            @click="selectTimeFilter(TimeType.TODAY)"
            :class="{ selected: timeFilterSelected === TimeType.TODAY }"
            >Today</span
          >
          <span
            @click="selectTimeFilter(TimeType.DAY)"
            :class="{ selected: timeFilterSelected === TimeType.DAY }"
            >24H</span
          >
          <span
            @click="selectTimeFilter(TimeType.SEVEN_DAY)"
            :class="{ selected: timeFilterSelected === TimeType.SEVEN_DAY }"
            >7D</span
          >
        </div>
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
import { TimeType } from '@/components/Alerts/alerts.constant'

const alertsStore = useAlertsStore()

onMounted(async () => {
  await alertsStore.fetchAlerts()
})

const timeFilterSelected = computed(() => alertsStore.timeSelected)
const selectTimeFilter = (type: TimeType | undefined) => {
  alertsStore.selectTime(type)
}

const searchAlerts = ref('')
const searchAlertsListener = (v: any) => {
  // need to define (with BE dev), when to send request, how many chars,...
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';

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

.time-search-filter {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.time-filter {
  display: flex;
  flex-direction: row;
  align-items: center;
  border-radius: vars.$border-radius-xs;
  border: 1px solid var(variables.$border-on-surface);
  background-color: var(variables.$background);
  height: 2.5rem;
  padding: 3px;
  > span {
    padding: 4px 15px;
    border-right: 1px solid var(variables.$border-on-surface);
    &:last-child {
      border-right: none;
    }
    &.selected {
      border-right-color: var(variables.$background);
      background-color: var(variables.$border-on-surface);
    }
    &:hover {
      cursor: pointer;
    }
  }
}

.search-filter {
  width: 30%;
  .search-alerts-input {
    width: 100%;
    :deep(.feather-input-border) {
      .pre-border,
      .label-border,
      .post-border {
        border-color: var(variables.$border-on-surface);
      }
    }
  }
}

.content {
  background: white;
  padding: var(variables.$spacing-l);
}
</style>
