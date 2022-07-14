<template>
  <table class="tl1 tl2 tl3" summary="Minions" data-test="minions-table">
    <thead>
      <tr>
        <th scope="col" data-test="col-status">Minion Status</th>
        <th scope="col" data-test="col-latency">Latency</th>
        <th scope="col" data-test="col-cpu-util">CPU Util</th>
      </tr>
    </thead>
    <TransitionGroup name="data-table" tag="tbody">
      <tr v-for="(minion, index) in minionQueries.listMinions" :key="minion.id" :data-index="index" data-test="minion-item">
        <td>{{ minion.status }}</td>
        <td>{{ minion.latency }}</td>
        <td>{{ minion.cpu_util }}</td>
      </tr>
    </TransitionGroup>
  </table>
</template>

<script setup lang="ts">
import { useMinionsQueries } from '@/store/Queries/minionsQueries'
const minionQueries = useMinionsQueries()
</script>

<style lang="scss">
@import "@featherds/table/scss/table";
table {
  @include table;
}
</style>

