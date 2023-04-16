<template>
  <div class="section-wrapper">
    <div class="section-headline">
      <div class="headline">{{ text }}</div>
      <div><slot name="count" /></div>
    </div>
    <div class="section-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps({
  text: {
    type: String,
    default: 'Section Headline'
  }
})
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.section-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-headline {
  display: flex;
  align-items: center;
}
.headline {
  @include typography.headline3();
  margin-right: var(variables.$spacing-s);
}
</style>
