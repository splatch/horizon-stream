<template>
  <div class="existing-items-container">
    <div class="title">
      <div>{{ title }}</div>
    </div>
    <div
      class="list"
      v-for="item in list"
    >
      <div
        class="card"
        @click="$emit('select-existing-item', item)"
        :class="{ selected: item.id === selectedItemId }"
      >
        {{ item.name }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MonitorPolicy, PolicyRule } from '@/types/graphql'

defineProps<{
  title: string
  list: MonitorPolicy[] | PolicyRule[]
  selectedItemId?: number
}>()
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@featherds/styles/mixins/elevation';
@use '@/styles/vars.scss';

.existing-items-container {
  @include elevation.elevation(2);
  display: flex;
  flex-direction: column;
  background: var(variables.$surface);
  margin: var(variables.$spacing-l) 0;
  padding: var(variables.$spacing-l);
  width: 350px;
  border-radius: vars.$border-radius-s;
  border-left: 5px solid var(variables.$success);

  .title {
    @include typography.headline3;
    display: flex;
    justify-content: space-between;
  }

  .list {
    display: flex;
    margin-top: var(variables.$spacing-xs);

    .card {
      @include typography.headline4;
      width: 100%;
      border: 1px solid var(variables.$shade-2);
      border-radius: vars.$border-radius-m;
      padding: var(variables.$spacing-xs) var(variables.$spacing-m);
      cursor: pointer;
    }

    .selected {
      background: var(variables.$shade-4);
    }
  }
}
</style>
