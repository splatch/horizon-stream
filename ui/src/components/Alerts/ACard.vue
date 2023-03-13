<template>
  <div class="card">
    <FeatherExpansionPanel>
      <div class="description">{{ alert.description }}</div>
    </FeatherExpansionPanel>
    <div class="expansion-title">
      <FeatherCheckbox
        :model-value="alert.isSelected"
        @update:model-value="alertSelectedHandler(alert.id)"
      />
      <div class="content">
        <div class="left">
          <div class="name">{{ alert.name }}</div>
          <div class="severity">{{ alert.severity }}</div>
          <div class="cause">{{ alert.cause }}</div>
        </div>
        <div class="center">
          <!-- duration: hrs, days, weeks. months? -->
          <div class="duration">{{ alert.duration }}</div>
          <div class="node">
            <Icon
              :icon="storageIcon"
              data-test="icon-storage"
            />
            <span>{{ alert.node }}</span>
          </div>
          <div class="date">{{ alert.date }}</div>
          <div class="time">{{ alert.time }}</div>
        </div>
        <div class="right">
          <FeatherCheckbox
            :model-value="acknowledgedChecked"
            @update:model-value="acknowledgeHandler(alert.id)"
            >Acknowledged</FeatherCheckbox
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import Storage from '@material-design-icons/svg/outlined/storage.svg'
import { IIcon } from '@/types'

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

const storageIcon: IIcon = {
  image: Storage,
  title: 'Node'
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';
@use '@/styles/mixins.scss';

.card {
  position: relative;
  margin-bottom: var(variables.$spacing-xs);
}

.expansion-title {
  position: absolute;
  top: 7px;
  left: var(variables.$spacing-m);
  width: 94%;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  :deep(> .layout-container) {
    margin-bottom: 0;
    .feather-checkbox {
      padding-right: var(variables.$spacing-xs);
      border-right: 5px solid red;
      margin-right: var(variables.$spacing-s);
      label {
        display: none;
      }
    }
  }
}

.content {
  width: 95%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.left {
  width: 42%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.center {
  width: 30%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.right {
  width: 15%;
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  align-items: center;
  :deep(.layout-container) {
    margin-bottom: 0;
    .feather-checkbox {
      padding-right: var(variables.$spacing-xs);
      margin-right: var(variables.$spacing-s);
    }
  }
}

.name {
  @include mixins.truncate-text();
}

.severity {
  padding: 3px 6px;
  border-radius: vars.$border-radius-xs;
  color: red;
  background-color: rgba(255, 0, 0, 0.3);
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
  .feather-expansion-header-button.expanded {
    height: 3rem;
  }
  .description {
    margin-left: var(variables.$spacing-xxl);
  }
}
</style>
