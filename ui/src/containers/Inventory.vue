<template>
  <PageHeader :heading="heading" class="header" data-test="page-header" />
  <FeatherTabContainer class="tab-container" data-test="tab-container">
    <template v-slot:tabs>
      <FeatherTab v-for="tab in tabs" :key="tab.label">{{ tab.label }}</FeatherTab>
    </template>
    <FeatherTabPanel>
      <Filter v-if="tabMonitoredContent.length" />
      <MonitoredNodesTabContent v-if="tabMonitoredContent.length" :tabContent="tabMonitoredContent" />
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
import MonitoredNodesTabContent from '@/components/Inventory/MonitoredNodesTabContent.vue'
import DetectedNodesTabContent from '@/components/Inventory/DetectedNodesTabContent.vue'
import { TimeUnit } from '@/types'
import { NodeContent } from '@/types/inventory'
import { useInventoryQueries } from '@/store/Queries/inventoryQueries'

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
      profileValue: 70,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      managementIpValue: '0.0.0.0',
      managementIpLink: 'goto',
      tagValue: 10,
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
      profileValue: 30,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      managementIpValue: '0.0.0.0',
      managementIpLink: 'goto',
      tagValue: 49,
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
      profileValue: 40,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      managementIpValue: '0.0.0.0',
      managementIpLink: 'goto',
      tagValue: 20,
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
      profileValue: 53,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      managementIpValue: '0.0.0.0',
      managementIpLink: 'goto',
      tagValue: 60,
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
      profileValue: 40,
      profileLink: 'goto',
      locationValue: 'DefaultMinion',
      locationLink: 'goto',
      managementIpValue: '0.0.0.0',
      managementIpLink: 'goto',
      tagValue: 62,
      tagLink: 'goto'
    }
  }
]

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

const inventoryQueries = useInventoryQueries()
const tabMonitoredContent = computed((): NodeContent[] => {
  const nodes = inventoryQueries.nodes
  
  if(!nodes[0]?.label) return []

  return [
    {
      id: nodes[0]?.id,
      label: nodes[0]?.label,
      metrics: [
        {
          ...nodes[0]?.metrics[0]
        },
        {
          type: 'uptime',
          label: 'Uptime',
          timestamp: null,
          timeUnit: TimeUnit.MSecs,
          status: ''
        },
        {
          type: 'status',
          label: 'Status',
          status: nodes[0]?.metrics[0].status || ''
        }
      ],
      anchor: {
        profileValue: 0,
        profileLink: '',
        locationValue: nodes[0]?.anchor.locationValue || '',
        locationLink: '',
        managementIpValue: nodes[0]?.anchor.managementIpValue || '',
        managementIpLink: '',
        tagValue: 0,
        tagLink: ''
      }
    }
  ]
})

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