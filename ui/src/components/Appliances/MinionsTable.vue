<template>
  <TableCard v-if="appliancesStore.minionsTableOpen">
    <div class="header">
      <div class="title">
        Minions ({{ applianceQueries.tableMinions.length }})
      </div>
      <FeatherButton 
        data-test="hide-minions-btn"
        icon="Hide Minions" 
        @click="appliancesStore.hideMinionsTable"
        v-if="!widgetProps?.isWidget"
      >
        <FeatherIcon :icon="ChevronLeft" />
      </FeatherButton>
    </div>
    <div class="container">
      <table class="tl1 tl2 tc3 tc4 tc5 data-table" summary="Minions" data-test="minions-table">
        <thead>
          <tr>
            <th scope="col" data-test="col-date">Time</th>
            <th scope="col" data-test="col-minion">Name</th>
            <th scope="col" data-test="col-latency">Latency</th>
            <th scope="col" data-test="col-uptime">Uptime</th>
            <th scope="col" data-test="col-status">Status</th>
          </tr>
        </thead>
        <TransitionGroup name="data-table" tag="tbody">
          <tr v-for="(minion, index) in listMinionsWithBgColor" :key="(minion.id as string)" :data-index="index" data-test="minion-item">
            <td>{{ minion.lastUpdated }}</td>
            <td>{{ minion.id }}</td>
            <td>
              <div @click="openLatencyGraph(minion)" :data-metric="minion.icmp_latency" class="bg-status pointer" :class="minion.latencyBgColor" data-test="minion-item-latency">
                {{ getHumanReadableDuration(minion.icmp_latency) }}
              </div>
            </td>
            <td>
              <div @click="openUptimeGraph(minion)" :data-metric="minion.snmp_uptime" class="bg-status pointer" :class="minion.uptimeBgColor" data-test="minion-item-uptime">
                {{ getHumanReadableDuration(minion.snmp_uptime) }}
              </div>
            </td>
            <td>
              <div class="bg-status" :class="minion.statusBgColor" data-test="minion-item-status">
                {{ minion.status }}
              </div>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>
  </TableCard>
  <PrimaryModal :visible="modal.isVisible" :title="modal.title" :hide-title="modal.hideTitle">
    <template #content>
      <Graph :graph="graphMetric" />
    </template>
    <template #footer>
      <FeatherButton primary @click="modal.isVisible = false">Close</FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script setup lang="ts">
import { useAppliancesQueries } from '@/store/Queries/appliancesQueries'
import { useAppliancesStore } from '@/store/Views/appliancesStore'
import ChevronLeft from '@featherds/icon/navigation/ChevronLeft'
import { ExtendedMinionDTOWithBGColors } from '@/types/minion'
import { ComputedRef } from 'vue'
import { formatItemBgColor, getHumanReadableDuration } from './utils'
import { WidgetProps } from '@/types'
import { GraphMetric } from '@/types/graphs'

defineProps<{widgetProps?: WidgetProps}>()

const appliancesStore = useAppliancesStore()
const applianceQueries = useAppliancesQueries()
const listMinionsWithBgColor: ComputedRef<ExtendedMinionDTOWithBGColors[]> = computed<any[]>(() => formatItemBgColor(applianceQueries.tableMinions))

const graphMetric = ref({} as GraphMetric)
const modal = ref({
  isVisible: false,
  title: '',
  hideTitle: true
})
const openLatencyGraph = (minion: any) => {
  modal.value = {
    ...modal.value,
    isVisible: true
  }
  
  const {id, location} = minion
  graphMetric.value = {
    label: 'Minion Latency',
    metrics: ['snmp_round_trip_time_msec'], // TODO: might be different once BE avail
    id,
    location
  }
}
const openUptimeGraph = (minion: any) => {
  modal.value = {
    ...modal.value,
    isVisible: true
  }
  
  const {id, location} = minion
  graphMetric.value = {
    label: 'Minion Uptime',
    metrics: ['minion_uptime_sec'], // TODO: might be different once BE avail
    id,
    location
  }
}
</script>

<style lang="scss" scoped>
@use "@featherds/styles/mixins/typography";
@use "@featherds/table/scss/table";
@use "@/styles/_statusBackground";

.header {
  display: flex;
  justify-content: space-between;
  .title {
    @include typography.headline3;
    margin-left: 15px;
  }
}

.container {
  display: block;
  overflow-x: auto;

  table {
    width: 100%;
    @include table.table;
    @include table.table-condensed;
    thead {
      background: var(typography.$background);
      text-transform: uppercase;
    }
    td {
      white-space: nowrap;
      div {
        border-radius: 5px;
        padding: 0px 5px 0px 5px;
      }
    }
  }
}
</style>
