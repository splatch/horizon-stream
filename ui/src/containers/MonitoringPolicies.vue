<template>
  <div class="container">
    <PageHeader heading="Monitoring Policies" />
    <FeatherTabContainer class="tab-container">
      <template v-slot:tabs>
        <FeatherTab>Create New Policy</FeatherTab>
        <FeatherTab>Existing Policies</FeatherTab>
      </template>
      <FeatherTabPanel class="panel"><AddNewPolicy /></FeatherTabPanel>
      <FeatherTabPanel class="panel"> Content for Policy List </FeatherTabPanel>
    </FeatherTabContainer>
  </div>
</template>

<script setup lang="ts">
import { FeatherTab, FeatherTabContainer, FeatherTabPanel } from '@featherds/tabs'
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';

.container {
  display: flex;
  margin: 0 1rem;
  flex-direction: column;

}
</style>
