<template>
  <h3>Minions</h3>
  <table class="tl1 tl2 tl3 data-table" summary="Minions" data-test="minions-table">
    <thead>
      <tr>
        <th scope="col" data-test="col-label">Label</th>
        <th scope="col" data-test="col-status">Status</th>
        <th scope="col" data-test="col-location">Location</th>
      </tr>
    </thead>
    <TransitionGroup name="data-table" tag="tbody">
      <tr v-for="(minion, index) in minionQueries.listMinions" :key="minion.id" :data-index="index" data-test="minion-item">
        <td>{{ minion.label }}</td>
        <td>{{ minion.status }}</td>
        <td>{{ minion.location }}</td>
      </tr>
    </TransitionGroup>
  </table>
</template>

<script setup lang="ts">
import { useMinionsQueries } from '@/store/Queries/minionsQueries'
const minionQueries = useMinionsQueries()
</script>


