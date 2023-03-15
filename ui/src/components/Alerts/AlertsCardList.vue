<template>
  <div class="card-list">
    <div class="card-list-top">
      <div class="select-all-checkbox-btns">
        <FeatherCheckbox
          v-model="isAllAlertsSelected"
          id="selectAll"
          data-test="select-all-checkbox"
          >Select All</FeatherCheckbox
        >
        <FeatherButton
          @click="clearAlerts"
          :disabled="!atLeastOneAlertSelected"
          text
          data-test="clear-btn"
          >clear</FeatherButton
        >
        <FeatherButton
          @click="acknowledgeSelectedAlerts"
          :disabled="!atLeastOneAlertSelected"
          text
          data-test="acknowledge-btn"
          >acknowledge</FeatherButton
        >
      </div>
      <FeatherPagination
        v-model="page"
        :pageSize="pageSize"
        :total="total"
        @update:pageSize="updatePageSize"
        data-test="pagination-top"
      />
    </div>
    <div class="content">
      <div
        v-if="alerts.length"
        data-test="alert-list"
      >
        <AlertsCard
          v-for="alert in alertsStore.alertList"
          :key="alert.id"
          :alert="alert"
          @alert-selected="alertSelectedListener"
        />
      </div>
      <div
        v-else
        class="empty-list"
        data-test="empty-list"
      >
        No results found. Refine or reduce filter criteria.
        <FeatherButton secondary>clear all filters</FeatherButton>
      </div>
    </div>
    <div class="card-list-bottom">
      <FeatherPagination
        v-model="page"
        :pageSize="pageSize"
        :total="total"
        @update:pageSize="updatePageSize"
        data-test="pagination-bottom"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAlertsStore } from '@/store/Views/alertsStore'
import { IAlert } from '@/types/alerts'

const alertsStore = useAlertsStore()

const page = ref(1)
const pageSize = ref(10)
const total = 100

const updatePageSize = (v: number) => {
  pageSize.value = v
}

const alerts = ref([] as IAlert[])

onMounted(async () => {
  await alertsStore.fetchAlerts()
  alerts.value = alertsStore.alertList.map((a: IAlert) => ({ ...a, isSelected: false }))
})

const atLeastOneAlertSelected = computed(() => alerts.value.some(({ isSelected }) => isSelected))

const isAllAlertsSelected = ref(false)
watch(isAllAlertsSelected, (isSelected) => {
  alerts.value = alerts.value.map((a) => ({
    ...a,
    isSelected: isSelected
  }))
})

const alertSelectedListener = (id: string) => {
  alerts.value = alerts.value.map((a) => {
    if (a.id === id) {
      a.isSelected = !a.isSelected // selection toggle
    }

    return a
  })
}

const clearAlerts = () => {
  // send request
}
const acknowledgeSelectedAlerts = () => {
  // send request
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';

.card-list-top {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: var(variables.$spacing-s) var(variables.$spacing-xl);
  background-color: var(variables.$background);
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s vars.$border-radius-s 0 0;
  :deep(> .feather-pagination) {
    border: 0;
    min-height: auto;
    padding-left: 0;
    > .range-text {
      margin-right: 0;
      min-width: auto;
    }
    > .per-page-text,
    > .page-size-select,
    > nav {
      display: none;
    }
  }
}

.select-all-checkbox-btns {
  display: flex;
  flex-direction: row;
  > button {
    margin-left: var(variables.$spacing-xl);
  }
}

.card-list-bottom {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  align-items: center;
  padding: var(variables.$spacing-s) 0 var(variables.$spacing-s) var(variables.$spacing-xl);
  border-width: 0 1px 1px;
  border-style: solid;
  border-color: var(variables.$border-on-surface);
  border-radius: 0 0 vars.$border-radius-s vars.$border-radius-s;
  > * {
    margin-left: var(variables.$spacing-xl);
  }
  :deep(> .feather-pagination) {
    border: 0;
    min-height: auto;
  }
}

:deep(.layout-container) {
  margin-bottom: 0;
}
</style>
