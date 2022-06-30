<template>
  <FeatherNavigationRail @update:modelValue="store.triggerNavRail()">
    <template v-slot:main>
      <FeatherRailItem
        :class="{ selected: isSelected('/') }"
        href="/"
        :icon="Home"
        title="Dashboard"
      />
      <FeatherRailItem
        :class="{ selected: isSelected('/appliances') }"
        href="/appliances"
        :icon="Appliances"
        title="Appliances"
      />
      <FeatherRailItem
        :class="{ selected: isSelected('/devices') }"
        href="/devices"
        :icon="Laptop"
        title="Devices"
      />
    </template>
  </FeatherNavigationRail>
</template>
<script
  setup
  lang="ts"
>
import { useLayoutStore } from '@/store/layoutStore'
import Home from '@featherds/icon/action/Home'
import Appliances from '@featherds/icon/hardware/Appliances'
import Laptop from '@featherds/icon/hardware/Laptop'

const store = useLayoutStore()
const route = useRoute()

const isSelected = (path: string) => path === route.fullPath
</script>

