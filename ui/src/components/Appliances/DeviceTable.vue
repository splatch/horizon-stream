<template>
  <div class="device-container">
    <div class="search-filter">
      <FeatherInput v-model="searchValue" type="text" label="Search device" :background="true" />
      <FeatherButton icon="Filter">
        <FeatherIcon :icon="FilterAlt" />
      </FeatherButton>
    </div>
    <div class="data-table">
      <TransitionGroup name="data-table" tag="div">
        <div class="card" v-for="(device) in listDevicesWithBgColor" :key="device.id" data-test="device-item">
          <div class="column name" data-test="col-device">
            {{ device.name }}
          </div>
          <div class="column" :class="device.latencyBgColor" data-test="col-latency">
            <pre class="title">ICMP Latency</pre>
            {{ device.icmp_latency }}
          </div>
          <div class="column" :class="device.uptimeBgColor" data-test="col-uptime">
            <pre class="title">SNMP Uptime</pre>
            {{ device.snmp_uptime }}
          </div>
          <div class="column" :class="device.statusBgColor" data-test="col-status">
            {{ device.status }}
          </div>
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<script setup lang="ts">
import FilterAlt from '@featherds/icon/action/FilterAlt'
import { useDeviceQueries } from '@/store/Queries/deviceQueries'
import { formatItemBgColor } from '@/helpers/formatting'

const deviceQueries = useDeviceQueries()
const listDevicesWithBgColor = computed(() => formatItemBgColor(deviceQueries.listDevices))

const searchValue = ''
</script>

<style lang="scss" scoped>
@import "@featherds/styles/themes/variables";
@import "@featherds/styles/mixins/elevation";
@import "@featherds/styles/mixins/typography";

.device-container {
  margin-left: var($spacing-xl)
}
.search-filter {
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin-top: var($spacing-xl);
  > .feather-input-container {
    width: 50%;
  }
}

.card {
  @include elevation(2);
  display: flex;
  margin-bottom: 2px;
  border-radius: 1px;
  
  div {
    display: flex;
    flex-direction: column;
    text-align: center;
    justify-content: center;
    width: 20%;
    padding: 8px;
    border-right: 1px solid var($shade-3);
    line-height: 15px;
    font-weight: bold;

    &.name {
      @include subtitle1;
      width: 40%;
      color: var($primary);
    }

    .title {
      font-family: inherit;
      font-size: 11px;
      font-weight: 100;
      margin: 0px;
    }
  }
}
</style>
