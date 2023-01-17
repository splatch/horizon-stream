<template>
  <div class="new-rule">
    <div class="title">New Rule</div>
    <div class="form-new-rule">
      <FeatherInput
        v-model="store.selectedRule.name"
        label="New Rule Name"
        class="name-rule"
      />
      <FeatherDropdown>
        <template v-slot:trigger="{ attrs, on }">
          <FeatherButton
            secondary
            class="btn-dropdown"
            v-bind="attrs"
            v-on="on"
          >
            <div class="btn-content">
              {{ componentType }}
              <FeatherIcon :icon="Icons.UnfoldMore" />
            </div>
          </FeatherButton>
        </template>
        <FeatherDropdownItem
          @click="selectedRuleType(compType)"
          v-for="(compType, index) of componentTypes"
          :key="index"
          >{{ compType }}
        </FeatherDropdownItem>
      </FeatherDropdown>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { FeatherButton } from '@featherds/button'
import { FeatherDropdown, FeatherDropdownItem } from '@featherds/dropdown'
import { FeatherIcon } from '@featherds/icon'
import UnfoldMore from '@featherds/icon/navigation/UnfoldMore'
import { markRaw } from 'vue'
import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'
const Icons = markRaw({
  UnfoldMore
})
const store = useMonitoringPoliciesStore()
const componentTypes = {
  1: 'CPU',
  2: 'Interface',
  3: 'Storage'
}
const componentType = ref(componentTypes['1'])

const selectedRuleType = (ruleType: string) => {
  componentType.value = ruleType
  store.selectedRule.componentType = ruleType
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';

.new-rule {
  .title {
    @include typography.headline4;
    margin-bottom: var(variables.$spacing-s);
  }
  .form-new-rule {
    display: flex;
    .name-rule {
      width: 350px;
      margin-right: var(variables.$spacing-l);
    }

    .btn-dropdown {
      height: 40px;
      .btn-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 200px;
      }
    }
  }
}

:deep(.feather-input-sub-text) {
  display: none;
}
</style>
