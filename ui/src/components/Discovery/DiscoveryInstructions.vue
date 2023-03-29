<template>
  <FeatherDrawer
    :modelValue="isOpen"
    :labels="{ close: 'close', title: 'Discovery' }"
    @update:modelValue="$emit('drawer-closed')"
  >
    <div class="drawerContent">
      <div class="section">
        <div class="title">Discovery</div>
        The discovery process identifies devices and entities on your monitored network through either active or passive
        discovery.
      </div>
      <br />
      <div class="section">
        <div class="title">What is Active Discovery?</div>
        Active discovery queries nodes and cloud APIs to detect the entities that you want to monitor. You can choose
        from two active discovery methods:
        <ul class="list">
          <li>
            <strong>ICMP/SNMP:</strong> Performs a ping sweep and scans for SNMP MIBs on nodes that respond. You can
            click Validate to verify that you have at least one IP address, range, or subnet in your inventory.
          </li>
          <li>
            <strong>Azure:</strong> Connects to the Azure API, queries the virtual machines list, and creates entities
            for each VM in the node inventory.
          </li>
        </ul>
        You can create multiple discovery events to target specific areas of your network.
        <ul class="list">
          <li><strong>Benefits:</strong> Can be more comprehensive than passive discovery.</li>
          <li>
            <strong>Disadvantages:</strong> Can slow network performance as the discovery process tries to connect to
            all devices.
          </li>
        </ul>
      </div>
      <div class="section">
        <div class="title">What is Passive Discovery?</div>

        Passive discovery uses Syslog and SNMP traps to identify network devices. It does so by monitoring their
        activity through events, flows, and indirectly by evaluating other devices' configuration settings.

        <p>Note that you can set only one passive discovery configuration.</p>

        <ul class="list">
          <li><strong>Benefits: </strong>Low bandwidth consumption.</li>
          <li>
            <strong>Disadvantages:</strong> May miss devices if they are not active. All devices must be enabled and
            configured to send Syslogs.
          </li>
        </ul>
      </div>
      <div class="section">
        <a
          href="https://docs.opennms.com/"
          target="_blank"
          class="link"
          >LEARN MORE</a
        >
      </div>
    </div>
  </FeatherDrawer>
</template>

<script setup lang="ts">
import { FeatherDrawer } from '@featherds/drawer'

const props = defineProps<{
  isOpen: boolean
}>()

const isOpen = computed<boolean>(() => props.isOpen)
</script>

<style lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@/styles/mediaQueriesMixins.scss';

.drawerContent {
  padding: var(variables.$spacing-m);
  width: 100%;
  @include mediaQueriesMixins.screen-md {
    width: 600px;
    padding: var(variables.$spacing-xl);
  }
  > .section {
    padding-bottom: var(variables.$spacing-m);
    > .title {
      @include typography.headline4;
    }
    > .list {
      padding-top: var(variables.$spacing-xl);
      padding-left: var(variables.$spacing-m);
      margin-left: var(variables.$spacing-xs);
      padding-bottom: var(variables.$spacing-xl);
      @include mediaQueriesMixins.screen-md {
        padding-left: var(variables.$spacing-xl);
      }
    }
    li {
      list-style: disc;
    }
    .link {
      @include typography.anchor;
      display: flex;
      justify-content: flex-end;
      cursor: pointer;
    }
  }
  p {
    padding-top: var(variables.$spacing-s);
  }
}
</style>
