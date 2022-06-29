<template>
  <table class="tl1 tl2 tl3" summary="Minions">
    <thead>
      <tr>
        <th scope="col">Minion Status</th>
        <th scope="col">Latency</th>
        <th scope="col">CPU Util</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="minion in minions" :key="minion.id">
        <td>{{ minion.status }}</td>
        <td>{{ minion.latency }}</td>
        <td>{{ minion.cpu_util }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import { Minion } from '@/types/appliances'
const minions = computed<Minion[]>(() => [])
</script>

<style lang="scss">
@import "@featherds/table/scss/table";
table {
  @include table;
}
</style>

