<template>
  <div class="azure-container">
    <div class="title">
      {{ Azure.title }}
    </div>

    <FeatherInput
      label="Azure Name"
      v-model="store.azure.name"
      class="name"
    />

    <div class="row">
      <FeatherInput
        v-model="store.azure.clientId"
        label="ClientID"
        class="column"
      />
      <FeatherProtectedInput
        v-model="store.azure.clientSecret"
        label="Client Secret"
        class="column"
      />
    </div>

    <div class="row">
      <FeatherInput
        v-model="store.azure.subscriptionId"
        label="SubscriptionID"
        class="column"
      />
      <FeatherInput
        v-model="store.azure.directoryId"
        label="DirectoryID"
        class="column"
      />
    </div>

    <!-- Placeholders -->
    <FeatherInput
      label="Locations"
      class="locations"
    />
    <FeatherInput
      label="Tags"
      class="tags"
    />

    <hr />
    <div class="buttons">
      <FeatherButton
        @click="store.clearAzureForm" 
        secondary>
        {{ Azure.cancelBtnText }}
      </FeatherButton>
      <FeatherButton
        @click="saveAzureDiscovery"
        primary>
        {{ Azure.saveBtnText }}
      </FeatherButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDiscoveryStore } from '@/store/Views/discoveryStore'
import { Azure } from './discovery.text'
import useSnackbar from '@/composables/useSnackbar'

const store = useDiscoveryStore()
const { showSnackbar } = useSnackbar()

const props = defineProps<{
	successCallback: (name: string) => void
}>()

const saveAzureDiscovery = async () => {
  const success = await store.saveDiscoveryAzure()
  if (success) {
    showSnackbar({
      msg: `${store.azure.name} setup successfully.`
    })

    props.successCallback(store.azure.name)
  }
}
</script>

<style scoped lang="scss">
@use "@/styles/mediaQueriesMixins";
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";
.azure-container {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-bottom: var(variables.$spacing-xl);

  .title {
    @include typography.headline4;
    margin-bottom: var(variables.$spacing-s);
  }

  hr {
    width: 100%;
    margin-bottom: var(variables.$spacing-xl);
    border-color: var(variables.$shade-4)
  }

  .buttons {
    flex-direction: row;
    align-self: flex-end;
  }

  @include mediaQueriesMixins.screen-md {
    .name, .locations, .tags {
      width: calc(50% - var(variables.$spacing-s))
    }
    .row {
      display: flex;
      gap: var(variables.$spacing-l);
      .column {
        flex: 1;
      }
    }
  }
}
</style>
