<template>
  <PageHeader :heading="heading" class="header" />
  <FeatherTabContainer class="tab-container">
    <template v-slot:tabs>
      <FeatherTab v-for="tab in tabs" :key="tab.label">{{ tab.label }}</FeatherTab>
    </template>
    <FeatherTabPanel>
      <Filter v-if="tabMonitoredContent?.length" />
      <MonitoredNodesTabContent :tabContent="tabMonitoredContent" />
    </FeatherTabPanel>
    <FeatherTabPanel>
      <Filter v-if="tabUnmonitoredContent?.length" />
      <MonitoredNodesTabContent :tabContent="tabUnmonitoredContent" />
    </FeatherTabPanel>
    <FeatherTabPanel>
      <Filter v-if="tabDetectedContent?.length" />
      <DetectedNodesTabContent :tabContent="tabDetectedContent" />
    </FeatherTabPanel>
  </FeatherTabContainer>
</template>

<script lang="ts" setup>
import {
  FeatherTab,
  FeatherTabContainer,
  FeatherTabPanel
} from '@featherds/tabs'
import Filter from '@/components/Inventory/Filter.vue'
import DetectedNodesTabContent from '@/components/Inventory/DetectedNodesTabContent.vue'
import MonitoredNodesTabContent from '@/components/Inventory/MonitoredNodesTabContent.vue'
import { TimeUnit } from '@/types'
import { useInventoryQueries } from '@/store/Queries/inventoryQueries'
import { NodeContent } from '@/types/inventory'

const unmonitoredContent: NodeContent[] = [
  {
    id: 1,
    label: 'Unmonitored Node 1',
    metrics: [
      {
        type: 'latency',
        label: 'Latency',
        timestamp: 9,
        timeUnit: TimeUnit.MSecs,
        status: 'UP'
      },
      {
        type: 'uptime',
        label: 'Uptime',
        timestamp: 1667930274.660,
        timeUnit: TimeUnit.Secs,
        status: 'DOWN'
      },
      {
        type: 'status',
        label: 'Status',
        status: 'DOWN'
      }
    ],
    anchor: {
      profileValue: 75,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      ipInterfaceValue: 25,
      ipInterfaceLink: 'goto',
      tagValue: 100,
      tagLink: 'goto'
    }
  },
  {
    id: 2,
    label: 'Unmonitored Node 2',
    metrics: [
      {
        type: 'latency',
        label: 'Latency',
        timestamp: 9,
        timeUnit: TimeUnit.MSecs,
        status: 'UP'
      },
      {
        type: 'uptime',
        label: 'Uptime',
        timestamp: 1667930274.660,
        timeUnit: TimeUnit.Secs,
        status: 'DOWN'
      },
      {
        type: 'status',
        label: 'Status',
        status: 'DOWN'
      }
    ],
    anchor: {
      profileValue: 75,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      ipInterfaceValue: 25,
      ipInterfaceLink: 'goto',
      tagValue: 100,
      tagLink: 'goto'
    }
  }
]
const detectedContent: NodeContent[] = [
  {
    id: 1,
    label: 'Detected Node 1',
    metrics: [
      {
        type: 'latency',
        label: 'Latency',
        timestamp: 9,
        timeUnit: TimeUnit.MSecs,
        status: 'UP'
      },
      {
        type: 'uptime',
        label: 'Uptime',
        timestamp: 1667930274.660,
        timeUnit: TimeUnit.Secs,
        status: 'DOWN'
      },
      {
        type: 'status',
        label: 'Status',
        status: 'DOWN'
      }
    ],
    anchor: {
      profileValue: 75,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      ipInterfaceValue: 25,
      ipInterfaceLink: 'goto',
      tagValue: 100,
      tagLink: 'goto'
    }
  },
  {
    id: 2,
    label: 'Detected Node 2',
    metrics: [
      {
        type: 'latency',
        label: 'Latency',
        timestamp: 9,
        timeUnit: TimeUnit.MSecs,
        status: 'UP'
      },
      {
        type: 'uptime',
        label: 'Uptime',
        timestamp: 1667930274.660,
        timeUnit: TimeUnit.Secs,
        status: 'DOWN'
      },
      {
        type: 'status',
        label: 'Status',
        status: 'DOWN'
      }
    ],
    anchor: {
      profileValue: 75,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      ipInterfaceValue: 25,
      ipInterfaceLink: 'goto',
      tagValue: 100,
      tagLink: 'goto'
    }
  },
  {
    id: 3,
    label: 'Detected Node 3',
    metrics: [
      {
        type: 'latency',
        label: 'Latency',
        timestamp: 9,
        timeUnit: TimeUnit.MSecs,
        status: 'UP'
      },
      {
        type: 'uptime',
        label: 'Uptime',
        timestamp: 1667930274.660,
        timeUnit: TimeUnit.Secs,
        status: 'DOWN'
      },
      {
        type: 'status',
        label: 'Status',
        status: 'DOWN'
      }
    ],
    anchor: {
      profileValue: 75,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      ipInterfaceValue: 25,
      ipInterfaceLink: 'goto',
      tagValue: 100,
      tagLink: 'goto'
    }
  }
]

const nodesQueries = useInventoryQueries()
const heading = 'Network Inventory'
const tabs = [
  {
    label: 'Monitored Nodes'
  },
  {
    label: 'Umonitored Nodes'
  },
  {
    label: 'Detected Nodes'
  }
]
const tabMonitoredContent = computed(() => nodesQueries.nodes as NodeContent[])
const tabUnmonitoredContent = unmonitoredContent
const tabDetectedContent = detectedContent
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.header {
  margin-right: var(variables.$spacing-l);
  margin-left: var(variables.$spacing-l);
}

.tab-container {
  margin: 0 var(variables.$spacing-l);
  :deep(> ul) {
    display: flex;
    border-bottom: 1px solid var(variables.$secondary-text-on-surface);
    > li {
      display: flex !important;
      flex-grow: 1;
      > button {
        display: flex;
        flex-grow: 1;
        > span {
          flex-grow: 1;
        }
      }
    }
  }
}
</style>