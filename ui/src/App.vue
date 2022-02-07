  
<template>
  <FeatherAppLayout content-layout="full">
    <template v-slot:header>
      <Menubar />
    </template>

    <template v-slot:rail>
      <NavigationRail />
    </template>

    <Spinner />
    <router-view />
  </FeatherAppLayout>
</template>
  
<script setup lang="ts">
import { FeatherAppLayout } from '@featherds/app-layout'
import Menubar from '@/components/Layout/Menubar.vue'
import Spinner from '@/components/Common/Spinner.vue'
import NavigationRail from '@/components/Layout/NavigationRail.vue'
</script>
  
<style lang="scss">
@import "@featherds/styles/lib/grid";
@import "@featherds/styles/mixins/typography";
@import "@featherds/styles/themes/open-mixins";

a {
  text-decoration: none;
  color: var($primary);
}
</style>
