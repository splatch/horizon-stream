<template>
  <div class="container">
    <PageHeadline text="Alerts" />
    <!-- severity filter -->
    <div>
      <!-- sort -->
      <!-- search -->
    </div>
    <!-- severity chip list -->
    <ACardList />
  </div>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.container {
  margin: 0 var(variables.$spacing-l);
}
</style>
