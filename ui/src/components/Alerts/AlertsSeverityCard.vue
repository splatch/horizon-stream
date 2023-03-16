<template>
  <div
    class="card border pointer"
    :class="{ selected: !isAdd }"
    @click="addSeverityFilter"
  >
    <div class="row">
      <div><AlertsSeverityLabel :severity="severity" /></div>

      <Transition name="icon-anim">
        <FeatherIcon
          :icon="isAdd ? Add : Cancel"
          class="icon"
          focusable="false"
        />
      </Transition>
    </div>
    <div class="count">{{ count }}</div>
  </div>
</template>

<script lang="ts" setup>
import Add from '@featherds/icon/action/Add'
import Cancel from '@featherds/icon/navigation/Cancel'
import { useAlertsStore } from '@/store/Views/alertsStore'

const store = useAlertsStore()

const isAdd = ref(true)
const props = defineProps<{
  severity: string
  count: number
}>()

const addSeverityFilter = () => {
  isAdd.value = !isAdd.value
  store.toggleSeverity(props.severity)
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@/styles/mediaQueriesMixins.scss';

.card {
  background-color: var(variables.$surface);
  min-width: 165px;
  height: 85px;
  padding: var(variables.$spacing-s);
  border-radius: 5px;
  @include mediaQueriesMixins.screen-md {
    height: 130px;
  }
  &.selected {
    background-color: var(variables.$shade-4);
    border-color: var(variables.$secondary-variant);
  }

  > .row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    > .icon {
      font-size: 24px;
    }
  }

  > .count {
    margin-top: var(variables.$spacing-xs);
    @include typography.headline2();
    @include mediaQueriesMixins.screen-md {
      @include typography.display2();
      font-weight: 300;
    }
  }
}

.icon-anim-enter-active {
  transition: all 0.3s ease-out;
}

.icon-anim-leave-active {
  transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}

.icon-anim-enter-from,
.icon-anim-leave-to {
  transform: rotateZ(90deg);
  opacity: 0;
}
</style>
