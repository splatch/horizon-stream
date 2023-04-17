<template>
  <router-link
    active-class="selected"
    custom
    :to="href"
    v-slot="{ isActive, href, navigate }">
    <FeatherAppRailNavItem
      :icon="icon"
      :title="title"
      @click="navigate"
      :href="href"
      :class="{ selected: isActive }"/>
  </router-link>
</template>

<script setup lang="ts">

import { FeatherAppRailNavItem } from '@featherds/app-rail'

const props = defineProps({
  icon: {
    type: Object,
    required: true
  },
  href: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  }
})

</script>
