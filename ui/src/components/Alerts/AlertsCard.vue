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
        <div class="name-node-type">
          <div
            class="name headline"
            data-test="name"
          >
            {{ alert.name }}
          </div>
          <div data-test="node-type">
            {{ alert.nodeType }}
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
          <div>{{ alert.cause }}</div>
        </div>
        <!-- duration: hrs, days, weeks. months? -->
        <div
          class="duration headline"
          data-test="duration"
        >
          <div>{{ alert.duration }}</div>
        </div>
        <div class="date-time">
          <div
            class="date headline"
            data-test="date"
          >
            <span>{{ alert.date }}</span>
          </div>
          <div
            class="time"
            data-test="time"
          >
            {{ alert.time }}
          </div>
        </div>
        <div class="check-circle">
          <FeatherIcon
            :icon="checkCircleIcon"
            :class="alert.isAcknowledged ? 'acknowledged' : ''"
            class="acknowledged-icon"
            data-test="check-icon"
          />
        </div>
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

const checkCircleIcon = markRaw(CheckCircle)
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@/styles/vars.scss';
@use '@/styles/mixins.scss';

.headline {
  @include typography.headline4();
}

.card {
  position: relative;
}

.expansion-title {
  position: absolute;
  top: 13px;
  left: var(variables.$spacing-xl);
  width: 900px;
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
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: flex-start;
}

.name-node-type {
  width: 20%;
  margin-right: 2%;
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
  .name {
    @include mixins.truncate-text();
  }
}

.severity {
  width: 20%;
  display: flex;
  justify-content: center;
  margin-right: 2%;
  &.error {
    :deep(.chip) {
      color: var(variables.$primary-text-on-color);
      background-color: var(variables.$error);
      margin: 0;
    }
  }
}

.cause {
  width: 20%;
  margin-right: 2%;
  display: flex;
  justify-content: flex-start;
  > div {
    @include mixins.truncate-text();
  }
}

.duration {
  width: 8%;
  margin-right: 2%;
  display: flex;
  justify-content: flex-start;
}

.date-time {
  width: 15%;
  margin-right: 2%;
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
}

.check-circle {
  width: 4%;
  display: flex;
  justify-content: center;
  .acknowledged-icon {
    width: 1.5rem;
    height: 1.5rem;
    margin-top: 0.8rem;
    color: var(variables.$shade-3);
    &.acknowledged {
      color: var(variables.$success);
    }
  }
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
