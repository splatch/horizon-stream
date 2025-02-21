<template>
  <div class="container">
    <div class="header">
      <HeadlinePage text="Insights Dashboard" />
      <DashboardHeaderLinks />
    </div>
    <div class="section-title">{{ dashboardText.Alerts.title }}</div>
    <div class="section-subtitle">{{ dashboardText.Alerts.timePeriod }}</div>
    <div class="list-alerts">
      <AlertsSeverityFilters
        @click="redirect('Alerts')"
        :timeRange="TimeRange.Last_24Hours"
      />
    </div>
    <div class="graphs">
      <DashboardCard
        :texts="dashboardText.NetworkTraffic"
        :redirectLink="'Inventory'"
      >
        <template v-slot:content>
          <DashboardNetworkTraffic />
        </template>
      </DashboardCard>
      <DashboardCard
        :texts="dashboardText.TopApplications"
        :redirectLink="'Flows'"
      >
        <template v-slot:content>
          <DashboardApplications />
        </template>
      </DashboardCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useFlowsStore } from '@/store/Views/flowsStore'
import dashboardText from '@/components/Dashboard/dashboard.text'
import { TimeRange } from '@/types/graphql'
import { useFlowsApplicationStore } from '@/store/Views/flowsApplicationStore'

const router = useRouter()
const flowsStore = useFlowsStore()
const flowsAppStore = useFlowsApplicationStore()

const redirect = (route: string) => {
  router.push(route)
}

onMounted(async () => {
  await flowsAppStore.getApplicationDataset()
  await flowsStore.getExporters()
})
</script>

<style scoped lang="scss">
@use '@featherds/styles/mixins/typography';
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';

.container {
  display: flex;
  margin: 0 1rem;
  flex-direction: column;
  width: 100%;
  padding: var(variables.$spacing-l) var(variables.$spacing-m);
  margin: auto;
  @include mediaQueriesMixins.screen-md {
    padding: var(variables.$spacing-xl);
  }
  > .header {
    @include typography.headline2();
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }
  .list-alerts {
    overflow-x: auto;
  }
  .section-title {
    @include typography.headline3();
  }
  .section-subtitle {
    @include typography.body-small;
    padding-bottom: var(variables.$spacing-xs);
  }
  .graphs {
    display: flex;
    gap: 1.3%;
    flex-direction: column;
    @include mediaQueriesMixins.screen-lg {
      flex-direction: row;
    }
  }
}
</style>
