<template>
  <div class="card">
    <div class="header">
      <div class="section-title">{{ texts.title }}</div>
      <div
        class="link"
        @click="router.push('Appliances')"
      >
        {{ texts.linkFlows }}
      </div>
    </div>
    <div class="section-subtitle">{{ texts.timePeriod }}</div>
    <div class="content">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  texts: Record<string, string>
}>()
</script>

<style scoped lang="scss">
@use '@featherds/styles/mixins/typography';
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';

.card {
  width: 100%;
  background-color: var(variables.$surface);
  padding: var(variables.$spacing-l);
  border: 1px solid var(variables.$border-on-surface);
  @include mediaQueriesMixins.screen-md {
    width: 49.5%;
  }
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .link {
      @include typography.subtitle2();
      text-decoration: underline;
      cursor: pointer;
      color: var(variables.$primary);
    }
    .section-title {
      @include typography.headline3();
    }
  }

  .content {
    margin-top: var(variables.$spacing-l);
    border: 1px solid var(variables.$border-on-surface);
    padding: var(variables.$spacing-l);
  }

  .empty {
    height: 630px;
    text-align: center;
    padding-top: 50px;
  }
}

.section-subtitle {
  @include typography.caption();
}
</style>
