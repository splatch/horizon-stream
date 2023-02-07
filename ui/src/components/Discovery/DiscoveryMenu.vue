<template>
  <div>
    Select a discovery
    <div class="discoveries">
      <div class="discovery-card">
        <FeatherRadioGroup
          vertical
          :label="'Active Discovery'"
          v-model="discovery"
        >
          <div class="row">
            <FeatherIcon
              class="icon"
              :icon="Network"
            />
            <FeatherRadio :value="snmp">ICMP/SNMP</FeatherRadio>
          </div>
          <div class="row">
            <FeatherIcon
              class="icon"
              :icon="Cloud"
            /><FeatherRadio :value="azure">AZURE</FeatherRadio>
          </div>
        </FeatherRadioGroup>
      </div>
      <div class="discovery-card passive">
        <FeatherRadioGroup
          horizontal
          :label="'Passive Discovery'"
          v-model="discovery"
        >
          <div class="row">
            <FeatherIcon
              class="icon"
              :icon="Network"
            />
            <FeatherRadio :value="syslog">SYSLOG</FeatherRadio>
            <FeatherRadio :value="snmpTraps">SNMP Traps</FeatherRadio>
          </div>
        </FeatherRadioGroup>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Cloud from '@featherds/icon/action/Cloud'
import Network from '@featherds/icon/hardware/Network'
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@/styles/vars.scss';
@use '@/styles/mediaQueriesMixins';

.discoveries {
  padding: var(variables.$spacing-l);
  background-color: vars.$shade-5;
  display: flex;
  margin-top: var(variables.$spacing-xs);

  .discovery-card {
    width: 50%;

    &.passive {
      border-left: 1px solid var(variables.$shade-4);
      padding-left: var(variables.$spacing-l);
    }
  }
  .row {
    display: flex;
    align-items: center;
    > .layout-container {
      padding-right: var(variables.$spacing-xxl);
    }
  }
  .icon {
    width: 28px;
    height: 28px;
    color: var(variables.$shade-2);
    margin-right: var(variables.$spacing-l);
  }
}

.layout-container {
  margin: 0 !important;
}
:deep(.feather-radio-group-container label) {
  margin-bottom: var(variables.$spacing-xs) !important;
}

:deep(.feather-input-sub-text) {
  display: none;
}
</style>
