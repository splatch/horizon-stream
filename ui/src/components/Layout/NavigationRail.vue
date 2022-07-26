<template>
  <FeatherNavigationRail @update:modelValue="store.triggerNavRail()">
    <template v-slot:main>
      <FeatherRailItem
        :class="{ selected: isSelected('/') }"
        href="/"
        :icon="Server"
        title="Appliances"
      />
    </template>
  </FeatherNavigationRail>
</template>

<script setup lang=ts>
import { useLayoutStore } from '@/store/Views/layoutStore'
import Server from '@featherds/icon/hardware/Server'

const store = useLayoutStore()
const route = useRoute()

const isSelected = (path: string) => path === route.fullPath
</script>

