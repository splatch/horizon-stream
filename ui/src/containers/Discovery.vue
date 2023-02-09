<template>
  <PageHeader
    heading="Discovery"
    class="header"
  />
  <div class="container">
    <section class="my-discovery">
      <div class="add-btn">
        <FeatherButton
          @click="addDiscovery"
          primary
        >
          New discovery
          <template #icon>
            <Icon :icon="addIcon" />
          </template>
        </FeatherButton>
      </div>

      <div class="my-discovery-inner">
        <FeatherAutocomplete
          class="search"
          v-model="discoveries.value"
          :loading="discoveries.loading"
          :results="discoveries.results"
          @search="discoveries.search"
          label="Search Discovery"
          type="single"
        />
        <DiscoveryListCard
          title=" My Active Discoveries"
          :list="[
            { id: 1, name: 'MAD-001' },
            { id: 2, name: 'MAD-002' }
          ]"
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
      v-if="isFormShown"
      class="discovery"
    >
      <!--DISCOVERY MENU -->
      <EditDiscovery />
    </section>
  </div>
</template>

<script lang="ts" setup>
import { IAutocompleteItemType } from '@featherds/autocomplete'
import AddIcon from '@featherds/icon/action/Add'
import { IIcon } from '@/types'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import { DiscoverytType, IDiscoverySNMPInput } from '@/types/discovery'
const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const addIcon: IIcon = {
  image: markRaw(AddIcon)
}
const isFormShown = ref(true)

const formInput = ref<IDiscoverySNMPInput>({
  type: DiscoverytType.ICSNMP,
  name: '', // required?
  location: 'Default',
  IPRange: '', // required?
  communityString: '', // required?
  UDPPort: 0 // required?
})

const saveHandler = () => {
  // startSpinner()
  // add query
  // if success
  // stopSpinner()
  // if error
  // showSnackbar({
  // msg: 'Save unsuccessfully!'
  // })
}

const cancelHandler = () => {
  formInput.value.type = DiscoverytType.None
}
const addDiscovery = () => {
  isFormShown.value = true
}

// Search discoveries
const discoveries = {
  loading: false,
  results: [] as IAutocompleteItemType[],
  value: [] as IAutocompleteItemType[],
  items: [],
  search: (q: string) => {
    discoveries.loading = true
    discoveries.results = discoveries.items
      .filter((x) => x.toLowerCase().indexOf(q) > -1)
      .map((x) => ({
        _text: x
      }))
    discoveries.loading = false
  }
}

const showDiscovery = (id: number) => {
  console.log('show discovery', id)
}
</script>

<style lang="scss" scope>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';
@use '@featherds/styles/mixins/typography';

.container {
  display: flex;
  flex-direction: column;
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);

  @include mediaQueriesMixins.screen-md {
    column-gap: var(variables.$spacing-l);
    flex-direction: row;
  }
}

.my-discovery {
  width: 100%;
  margin-bottom: var(variables.$spacing-l);
  border-bottom: 1px solid var(variables.$border-on-surface);

  .add-btn {
    width: 100%;
    margin-bottom: var(variables.$spacing-l);
    border-bottom: 1px solid var(variables.$border-on-surface);
    > button {
      margin-bottom: var(variables.$spacing-l);
    }
  }

  > .my-discovery-inner {
    display: flex;
    flex-direction: column;
    width: 100%;
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

  @include mediaQueriesMixins.screen-sm {
    flex-direction: row;
    column-gap: 2%;
    border-bottom: none;
  }
  @include mediaQueriesMixins.screen-md {
    flex-direction: column;
    margin-bottom: 0;
    width: 25%;
    min-width: 260px;
  }
}

.title {
  @include typography.subtitle1;
  display: flex;

  .count {
    background-color: #00666d1f;
    padding: 0 var(variables.$spacing-xs);
    margin-left: var(variables.$spacing-m);
    border-radius: 5px;
    @include typography.body-small;
  }
}

.emtpy {
  display: flex;
  gap: 8px;
  margin-top: var(variables.$spacing-s);

  .icon {
    width: 24px;
    height: 24px;
    color: var(variables.$shade-1);
  }
}

.feather-input-sub-text {
  display: none !important;
}

.discovery {
  width: 100%;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  background-color: var(variables.$surface);

  @include mediaQueriesMixins.screen-md {
    margin-bottom: 0;
  }
}

.header {
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);
}
</style>
