<template>
  <PageHeadline
    :text="discoveryText.Discovery.pageHeadline"
    class="page-headline"
  />
  <div class="container">
    <section class="my-discovery">
      <div class="add-btn">
        <FeatherButton
          @click="showDiscoveryEditing"
          primary
        >
          {{ discoveryText.Discovery.button.add }}
          <template #icon>
            <Icon :icon="addIcon" />
          </template>
        </FeatherButton>
      </div>

      <div class="my-discovery-inner">
        <FeatherAutocomplete
          class="search"
          v-model="searchValue"
          :loading="searchLoading"
          :results="discoveriesResults"
          @search="search"
          label="Search Discovery"
          type="single"
        />
        <DiscoveryListCard
          title=" My Active Discoveries"
          :list="store.activeDiscoveries"
          @select-discovery="showDiscovery"
        />
        <DiscoveryListCard
          title=" My Passive Discoveries"
          :list="[]"
        />
      </div>
    </section>

    <!-- add/edit a discovery  -->
    <section
      v-if="isDiscoveryEditingShown"
      class="discovery"
    >
      <div
        v-if="showNewDiscovery"
        class="type-selector"
      >
        <div class="headline">{{ discoveryText.Discovery.headline1 }}</div>
        <DiscoveryTypeSelector @discovery-option-selected="(type: string) => (discoverySelectedType = type)" />
      </div>
      <div>
        <div v-if="discoverySelectedType === DiscoveryType.ICMP">
          <DiscoverySnmpForm
            @close-form="handleCancel"
            :discovery="selectedDiscovery"
          />
        </div>
        <div v-else-if="discoverySelectedType === DiscoveryType.Azure">AZURE</div>
        <div v-else-if="discoverySelectedType === DiscoveryType.SyslogSNMPTraps">
          SyslogSNMPTraps
          <!-- <DiscoverySyslogSNMPTrapsForm
        v-if="discoverySelectedType === DiscoveryType.SyslogSNMPTraps"
        @cancel-editing="discoverySelectedType = DiscoveryType.None"
      /> -->
        </div>
        <div
          v-else
          class="get-started"
        >
          {{ discoveryText.Discovery.noneDiscoverySelectedMsg }}
        </div>
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { IAutocompleteItemType } from '@featherds/autocomplete'
import PageHeadline from '@/components/Common/PageHeadline.vue'
import AddIcon from '@featherds/icon/action/Add'
import { IIcon } from '@/types'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import { IDiscovery } from '@/types/discovery'
import { DiscoveryType } from '@/components/Discovery/discovery.constants'
import discoveryText from '@/components/Discovery/discovery.text'
import { useDiscoveryStore } from '@/store/Views/discoveryStore'
const store = useDiscoveryStore()

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const addIcon: IIcon = {
  image: markRaw(AddIcon)
}

const isDiscoveryEditingShown = ref(false)
const showNewDiscovery = ref(false)
const selectedDiscovery = ref<IDiscovery>(null)
const discoverySelectedType = ref(DiscoveryType.None)

const showDiscoveryEditing = () => {
  isDiscoveryEditingShown.value = true
  showNewDiscovery.value = true
}

const discoveriesResults = ref<(IDiscovery & IAutocompleteItemType)[]>([])
const searchLoading = ref(false)
const searchValue = ref(undefined)

const search = (q: string) => {
  searchLoading.value = true
  const results = store.activeDiscoveries
    .filter((x) => x.name.toLowerCase().indexOf(q) > -1)
    .map((x) => ({
      _text: x.name,
      id: x.id,
      name: x.name
    }))
  discoveriesResults.value = results
  searchLoading.value = false
}

const showDiscovery = (discovery: IDiscovery) => {
  isDiscoveryEditingShown.value = true
  showNewDiscovery.value = false
  discoverySelectedType.value = discovery.type
  selectedDiscovery.value = discovery
  console.log(discoverySelectedType.value)
}

const handleCancel = () => {
  isDiscoveryEditingShown.value = false
  discoverySelectedType.value = DiscoveryType.None
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';

.page-headline {
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);
}

.container {
  display: flex;
  flex-direction: row;
  flex-flow: wrap;
  justify-content: space-between;
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);

  @include mediaQueriesMixins.screen-md {
    > .my-discovery {
      width: 30%;
      min-width: auto;
    }
    > .discovery {
      width: 67%;
      min-width: auto;
    }
  }
}

.my-discovery {
  width: 100%;
  min-width: 400px;
  .add-btn {
    width: 100%;
    margin-bottom: var(variables.$spacing-l);
    border-bottom: 1px solid var(variables.$border-on-surface);
    > button {
      margin-bottom: var(variables.$spacing-l);
    }
  }
  > .my-discovery-inner {
    width: 100%;
    display: flex;
    flex-direction: column;
    margin-bottom: var(variables.$spacing-l);
    > * {
      margin-bottom: var(variables.$spacing-m);
      &:last-child {
        margin-bottom: 0;
      }
    }

    @include mediaQueriesMixins.screen-md {
      margin-bottom: 0;
    }

    .search {
      background-color: var(variables.$surface);
      margin-bottom: var(variables.$spacing-m);
    }
  }

  @include mediaQueriesMixins.screen-md {
    flex-direction: column;
    margin-bottom: 0;
    > * {
      width: 100%;
      margin-bottom: var(variables.$spacing-m);
    }
  }
}

.feather-input-sub-text {
  display: none !important;
}

.discovery {
  width: 100%;
  min-width: 400px;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  background-color: var(variables.$surface);

  .headline {
    @include typography.header();
  }

  @include mediaQueriesMixins.screen-md {
    margin-bottom: 0;
  }
}

.type-selector {
  margin-bottom: var(variables.$spacing-l);
}

.get-started {
  width: 100%;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

:deep(.feather-input-sub-text) {
  display: none !important;
}
</style>
