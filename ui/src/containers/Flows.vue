<template>
  <div class="flows-container">
    <PageHeadline
    text="Flows"
    data-test="page-header"
    />
    <!-- Filter Area -->
    <div class="filters"></div>
    <!-- Chart Area -->
    <div class="flows"></div>
  </div>
  
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@/styles/vars';
@use '@/styles/mediaQueriesMixins.scss';
@use '@featherds/styles/mixins/typography';

.flows {
  width: 100%;
  min-width: 400px;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  background-color: var(variables.$surface);

  &-container{
      margin: var(variables.$spacing-m);
      @include mediaQueriesMixins.screen-md {
      margin: 40px 80px;
    }
  }
  
}
.filters {
    margin-bottom: var(variables.$spacing-m);
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: var(variables.$spacing-xl);
  }
</style>
