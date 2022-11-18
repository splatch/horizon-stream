<template>
  <section class="header">{{ heading }}</section>
</template>
<script lang="ts" setup>
defineProps({
  heading: {
    type: String,
    default: 'Page Heading'
  }
})
</script>
<style lang="scss" scoped>
@use "@featherds/styles/mixins/typography";
.header {
  @include typography.headline2();
  font-weight: bold;
  margin: 1.5rem 1rem;
}
</style>