<template>
  <FeatherButton 
    primary
    data-test="open-tags-btn" 
    @click="store.triggerTagsBox"
  > 
    <template v-slot:icon>
      <FeatherIcon 
        :icon="store.isTagsOpen ? upIcon : downIcon" 
        aria-hidden="true" 
        focusable="false" 
      />
      Tagging
    </template>
  </FeatherButton>
</template>

<script setup lang="ts">
import { useInventoryStore } from '@/store/Views/inventoryStore'
import ArrowDown from '@featherds/icon/navigation/ExpandMore'
import ArrowUp from '@featherds/icon/navigation/ExpandLess'

const store = useInventoryStore()

const upIcon = markRaw(ArrowUp)
const downIcon = markRaw(ArrowDown)
</script>
