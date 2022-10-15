<template>
  <div class="feather-row">
    <div class="feather-col-12">
      <div class="container">
        <div class="header">Graphs</div>
          <Graph
            v-if="store.minionUptimeDataReady"
            :label="'Minion Uptime'"
            :dataSets="(store.minionDataSets as any)"
          />
      </div>
    </div>
  </div>
</template>
  
<script setup lang="ts">
import { useGraphQueries } from '@/store/Queries/graphQueries'

const store = useGraphQueries()

onMounted(() => store.getMinionUptime())
</script>
  
<style scoped lang="scss">
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";

.container {
  border: 2px solid var(variables.$shade-4);
  margin-bottom: 15px;

  .header {
    @include typography.headline3();
    text-align: center;
    padding: 6px;
    background: var(variables.$shade-4);
  }
}
</style>
  