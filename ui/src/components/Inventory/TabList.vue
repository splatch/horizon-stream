<template>
  <FeatherTabContainer class="tab-container">
    <template v-slot:tabs>
      <FeatherTab v-for="tab in tabs" :key="tab.label">{{ tab.label }}</FeatherTab>
    </template>
    <Filter />
    <FeatherTabPanel v-for="tab in tabs" :key="tab.label">
      <Card v-for="node in tab.nodes" :key="node.header" :item="node" />
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
import Card from '@/components/Inventory/Card.vue'

interface Node {
  header: string
}

interface Tab {
  label: string,
  nodes: Node[]
}

const tabs: Tab[] = [
  {
    label: 'Monitored Nodes',
    nodes: [
      {
        header: 'Node 1'
      },
      {
        header: 'Node 11'
      }
    ]
  },
  {
    label: 'Unmonitored Nodes',
    nodes: [
      {
        header: 'Node 2'
      },
      {
        header: 'Node 22'
      }
    ]
  },
  {
    label: 'Detected Nodes',
    nodes: [
      {
        header: 'Node 3'
      },
      {
        header: 'Node 33'
      }
    ]
  }
]
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.tab-container {
  :deep(> ul) {
    display: flex;
    border-bottom: 1px solid var(variables.$disabled-text-on-surface);
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