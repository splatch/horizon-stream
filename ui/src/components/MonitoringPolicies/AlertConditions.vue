<template>
  <div class="alert-conditions">
    <div class="title">Set Alert Conditions</div>
    <FeatherButton
      primary
      @click="triggerWidget"
    >
      <FeatherIcon
        :icon="Add"
        class="icon"
      />
      Add
    </FeatherButton>
    <div id="conditions-conatiner"></div>
  </div>
</template>

<script lang="ts" setup>
import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'
import Add from '@featherds/icon/action/Add'
import { getCurrentInstance } from 'vue'
import { AppContext, createVNode, render } from 'vue'
const store = useMonitoringPoliciesStore()
const widgetContainer = () => document.getElementById('conditions-conatiner') as HTMLDivElement
const instance = getCurrentInstance()?.appContext as AppContext
const conditionAlertCountId = ref(0)

const triggerWidget = () => {
  conditionAlertCountId.value++
  const component = defineAsyncComponent(() => import('@/components/MonitoringPolicies/Condition.vue'))
  const div = document.createElement('div')
  div.id = 'condition-alert' + conditionAlertCountId.value
  const vNode = createVNode(component, { id: conditionAlertCountId.value })
  vNode.appContext = instance
  render(vNode, div)
  widgetContainer().appendChild(div)
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.alert-conditions {
  margin-top: var(variables.$spacing-xxl);

  .title {
    @include typography.headline4;
  }
  .icon {
    font-size: 20px;
  }
}
</style>
