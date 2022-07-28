<template>
  <Transition name="fade">
    <TableCard v-if="appliancesStore.minionsTableOpen">
      <div class="header">
        <div class="title">
          Minions ({{ minionsQueries.listMinions.length }})
        </div>
        <FeatherButton 
          data-test="hide-minions-btn"
          icon="Hide Minions" 
          @click="appliancesStore.hideMinionsTable"
        >
          <FeatherIcon :icon="ChevronLeft" />
        </FeatherButton>
      </div>

      <table class="tl1 tl2 tl3 tl4 tc5 data-table" summary="Minions" data-test="minions-table">
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
          <tr v-for="(minion, index) in listMinionsWithBgColor" :key="minion.id" :data-index="index" data-test="minion-item">
            <td>{{ minion.lastUpdated }}</td>
            <td>{{ minion.id }}</td>
            <td>{{ minion.icmp_latency }}</td>
            <td>{{ minion.snmp_uptime }}</td>
            <td>
              <div :class="minion.statusBgColor" data-test="minion-item-status">
                {{ minion.status }}
              </div>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </TableCard>
  </Transition>
</template>

<script setup lang="ts">
import { useMinionsQueries } from '@/store/Queries/minionsQueries'
import { useAppliancesStore } from '@/store/Views/appliancesStore'
import { formatItemBgColor } from '@/helpers/formatting'
import ChevronLeft from '@featherds/icon/navigation/ChevronLeft'

const appliancesStore = useAppliancesStore()
const minionsQueries = useMinionsQueries()
const listMinionsWithBgColor = computed(() => formatItemBgColor(minionsQueries.listMinions))
</script>

<style lang="scss" scoped>
@import "@featherds/table/scss/table";
@import "@featherds/styles/mixins/typography";

.header {
  display: flex;
  justify-content: space-between;
  .title {
    @include headline3;
    margin-left: 15px;
  }
}

table {
  @include table-condensed();
  overflow-x: scroll;
  display: block;

  thead {
    background: var($background);
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

// hide / show transition
.fade-enter-active {
  transition: all 0.2s;
}
.fade-leave-active {
  transition: all 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>