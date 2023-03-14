<template>
  <div class="card">
    <FeatherExpansionPanel>
      <div
        class="description"
        data-test="description"
      >
        {{ alert.description }}
      </div>
    </FeatherExpansionPanel>
    <div class="expansion-title">
      <FeatherCheckbox
        :model-value="alert.isSelected"
        @update:model-value="alertSelectedHandler(alert.id)"
        data-test="checkbox"
      />
      <div class="content">
        <div>
          <div
            class="name headline"
            data-test="name"
          >
            {{ alert.name }}
          </div>
          <div
            class="node"
            data-test="node"
          >
            {{ alert.node }}
          </div>
        </div>
        <div
          class="severity error"
          data-test="severity"
        >
          <FeatherChip>{{ alert.severity }}</FeatherChip>
        </div>
        <div
          class="cause headline"
          data-test="cause"
        >
          {{ alert.cause }}
        </div>
        <!-- duration: hrs, days, weeks. months? -->
        <div
          class="duration headline"
          data-test="duration"
        >
          {{ alert.duration }}
        </div>
        <div>
          <div
            class="date headline"
            data-test="date"
          >
            {{ alert.date }}
          </div>
          <div
            class="time"
            data-test="time"
          >
            {{ alert.time }}
          </div>
        </div>
        <FeatherIcon
          :icon="CheckCircle"
          :class="alert.isAcknowledged ? 'acknowledged' : ''"
          class="acknowledged-check-circle"
          data-test="check-icon"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import CheckCircle from '@featherds/icon/action/CheckCircle'

const emits = defineEmits(['alert-selected'])
const props = defineProps({
  alert: {
    type: Object,
    required: true
  }
})

const alertSelectedHandler = (id: number) => {
  emits('alert-selected', id)
}

const acknowledgedChecked = ref(props.alert.acknowledged)
const acknowledgeHandler = (id: number) => {
  acknowledgedChecked.value = !acknowledgedChecked.value
  // send request
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@/styles/vars.scss';
@use '@/styles/mixins.scss';

.headline {
  @include typography.headline4();
}

.acknowledged-check-circle {
  width: 1.5rem;
  height: 1.5rem;
  margin-top: 1rem;
  color: var(variables.$shade-3);
  &.acknowledged {
    color: var(variables.$success);
  }
}

.card {
  position: relative;
}

.expansion-title {
  position: absolute;
  top: 13px;
  left: var(variables.$spacing-xl);
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  :deep(> .layout-container) {
    margin-bottom: 0;
    .feather-checkbox {
      padding-right: var(variables.$spacing-xs);
      label {
        display: none;
      }
    }
  }
}

.content {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 2rem;
}

.name {
  @include mixins.truncate-text();
}

.severity {
  &.error {
    :deep(.chip) {
      color: var(variables.$primary-text-on-color);
      background-color: var(variables.$error);
    }
  }
}

.node {
  > span {
    margin-left: var(variables.$spacing-xs);
  }
}

.cause {
  font-weight: bold;
}

:deep(.feather-expansion) {
  box-shadow: none;
  background-color: unset;
  border-width: 0 1px 1px;
  border-style: solid;
  border-color: var(variables.$border-on-surface);
  .feather-expansion-header-button {
    height: 5rem;
  }
  .feather-expansion-header-button.expanded {
    height: 5rem;
  }
  .description {
    margin-top: 1rem;
    margin-left: 2.1rem;
  }
}
</style>
