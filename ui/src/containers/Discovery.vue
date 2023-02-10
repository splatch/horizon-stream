<template>
  <PageHeader
    :heading="discoveryText.Discovery.heading"
    class="header"
  />
  <div class="container">
    <section class="my-discovery">
      <div class="add-btn">
        <FeatherButton
          @click="addDiscovery"
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
          :list="mocksActiveList"
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
import { IDiscovery } from '@/types/discovery'
import discoveryText from '@/components/Discovery/discovery.text'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()
const discoveriesResults = ref<(IDiscovery & IAutocompleteItemType)[]>([])
const searchLoading = ref(false)
const searchValue = ref(undefined)
const mocksActiveList = [
  { id: 1, name: 'MAD-001' },
  { id: 2, name: 'MAD-002' }
] as IDiscovery[]
const addIcon: IIcon = {
  image: markRaw(AddIcon)
}
const isFormShown = ref(true)

const saveHandler = () => {
  // startSpinner()
  // contentEditableIPRef.value.validateAndFormat()
  // add query
  // if success
  // stopSpinner()
  // if error
  // showSnackbar({
  // msg: 'Save unsuccessfully!'
  // })
}

const addDiscovery = () => {
  isFormShown.value = true
}

const search = (q: string) => {
  searchLoading.value = true
  const results = mocksActiveList
    .filter((x) => x.name.toLowerCase().indexOf(q) > -1)
    .map((x) => ({
      _text: x.name,
      id: x.id,
      name: x.name
    }))
  discoveriesResults.value = results
  searchLoading.value = false
}

const showDiscovery = (id: number) => {
  console.log('show discovery', id)
}
</script>

<style lang="scss" scoped>
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
  > form {
    div[class$='-input'] {
      margin-bottom: var(variables.$spacing-m);
    }
  }

  @include mediaQueriesMixins.screen-xl {
    .content-editable-container {
      width: 100%;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: flex-end;
      > div {
        width: 32%;
      }
    }
  }
}

.header {
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);
}
</style>
