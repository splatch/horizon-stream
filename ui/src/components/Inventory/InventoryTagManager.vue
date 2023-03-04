<template>
  <div class="tag-manager">
    <section class="select-tags">
      <div class="top">
        <div class="heading-total-selected">
          <h4>Select Tags:</h4>
          <div class="total-selected">
            <div class="total">
              TOTAL: <span>{{ tags.length }}</span>
              <span class="pipe">|</span>
            </div>
            <div class="selected">
              SELECTED: <span>{{ tagsSelected.length }}</span>
            </div>
          </div>
        </div>
        <div class="search-add">
          <!-- Add tag -->
          <!-- <InputButtonPopover
            :icon="inputIcon"
            :handler="addTag"
            label="Add Tag"
          /> -->
          <!-- Search tags input -->
          <!-- <FeatherInput
            :modelValue="searchValue"
            @update:model-value="tagsFiltering"
            clear="clear"
            label="Search Tags"
            class="search"
          /> -->
          <!-- <FeatherButton text>Deselect all</FeatherButton> -->
          <FeatherButton
            @click="toggleSelectAll"
            secondary
            class="select-all-btn"
            >{{ isSelectingAll ? 'Deselect all' : 'Select all' }}</FeatherButton
          >
          <DiscoveryAutocomplete
            @items-selected="tagsSelectedListener"
            :get-items="tagQueries.getTagsSearch"
            :items="tagQueries.tagsSearched"
            label="Search/Add tags (optional)"
            ref="tagsAutocompleteRef"
            class="tags-autocomplete"
            data-test="tags-autocomplete"
          />
        </div>
      </div>
      <FeatherChipList
        v-if="tags.length"
        :key="tagsSelected.toString()"
        condensed
        label="Tags"
        class="tag-chip-list"
      >
        <FeatherChip
          v-for="(tag, index) in tags"
          :key="index"
          @click="tagStore.toggleTag(tag)"
          :class="{ selected: isTagSelected(tag.name as string) }"
          class="pointer"
        >
          {{ tag.name }}
        </FeatherChip>
      </FeatherChipList>
    </section>
    <section class="tag-nodes">
      <h4>Tag Nodes:</h4>
      <FeatherButton
        data-test="cancel-btn"
        secondary
        @click="setTagEditMode(false)"
      >
        Cancel
      </FeatherButton>
      <FeatherButton
        data-test="save-btn"
        primary
        @click="setTagEditMode(true)"
      >
        Add tags to node
      </FeatherButton>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useInventoryStore } from '@/store/Views/inventoryStore'
import { useInventoryQueries } from '@/store/Queries/inventoryQueries'
import { useNodeMutations } from '@/store/Mutations/nodeMutations'
import { useTagQueries } from '@/store/Queries/tagQueries'
import { useTagStore } from '@/store/Components/tagStore'
import { useTagMutations } from '@/store/Mutations/tagMutations'

const inventoryStore = useInventoryStore()
const tagQueries = useTagQueries()
const tagStore = useTagStore()

const searchValue = ref()
const tags = computed(() => tagStore.tags)
const tagsSelected = computed(() => tagStore.tagsSelected)
const isSelectingAll = ref(false)

const setTagEditMode = (isEdit: boolean) => {
  tagStore.setTagEditMode(isEdit)
}

const isTagSelected = (name: string): boolean =>
  tagsSelected.value.some(({ name: selectedTagName }) => selectedTagName === name)

const toggleSelectAll = () => {
  isSelectingAll.value = !isSelectingAll.value
  tagStore.selectAllTags(isSelectingAll.value)
}

watchEffect(() => {
  if (inventoryStore.isTagManagerOpen) tagQueries.fetchTags()
})
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use '@featherds/styles/mixins/typography';
@use '@/styles/vars';
@use '@/styles/mediaQueries';

.tag-manager {
  display: flex;
  flex-direction: row;
  flex-flow: wrap;
  justify-content: space-between;
  border: 1px solid var(variables.$secondary-text-on-surface);
  border-radius: vars.$border-radius-m;
  padding: var(variables.$spacing-m);
  margin-bottom: var(variables.$spacing-xxl);
  background-color: var(variables.$disabled-text-on-color);
  min-width: 480px;
  h4 {
    padding-top: 3px;
  }
}

.select-tags {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-width: 445px;
  .top {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    margin-bottom: var(variables.$spacing-m);
    .heading-total-selected {
      width: 35%;
      .total-selected {
        display: flex;
        flex-direction: column;
        padding-top: 8px;
        margin-bottom: var(variables.$spacing-l);
        .total {
          > span {
            font-weight: bold;
          }
          .pipe {
            color: var(variables.$secondary-text-on-surface);
            margin: 0 var(variables.$spacing-s);
            display: none;
          }
        }
        .selected {
          > span {
            font-weight: bold;
          }
        }
      }
    }

    .search-add {
      width: 50%;
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      justify-content: flex-end;
      .select-all-btn {
        order: 2;
      }
      .tags-autocomplete {
        min-width: 265px;
        order: 1;
      }
    }

    @include mediaQueries.screen-md {
      margin-bottom: 0;
      .heading-total-selected {
        .total-selected {
          flex-direction: row;
          .total {
            .pipe {
              display: inline;
            }
          }
        }
      }
      .search-add {
        width: 70%;
        .select-all-btn {
          order: 1;
          margin-top: 2px;
          margin-right: var(variables.$spacing-m);
        }
        .tags-autocomplete {
          order: 2;
        }
      }
    }

    @include mediaQueries.screen-xl {
      .heading-total-selected {
        width: 50%;
        display: flex;
        flex-direction: row;
        .total-selected {
          .total {
            margin-left: var(variables.$spacing-m);
          }
        }
      }
    }
  }
  .tag-chip-list {
    max-height: 100px;
    overflow-y: scroll;
  }

  @include mediaQueries.screen-lg {
    width: 75%;
    min-width: 0;
  }
  @include mediaQueries.screen-xxl {
    width: 80%;
  }
}

.tag-nodes {
  display: flex;
  flex-direction: row;
  width: 100%;
  min-width: 445px;
  margin-top: var(variables.$spacing-m);
  padding-top: var(variables.$spacing-m);
  border-top: 1px solid var(variables.$secondary-text-on-surface);

  :deep(.select-tag-nodes) {
    margin-left: var(variables.$spacing-m);
    > label {
      display: none;
    }
    .feather-input-sub-text {
      display: none;
    }
  }

  @include mediaQueries.screen-lg {
    width: 20%;
    min-width: 0;
    margin-top: 0;
    padding-top: 0;
    border-top: 0;
    border-left: 1px solid var(variables.$secondary-text-on-surface);
    padding-left: var(variables.$spacing-l);
    display: block;
  }

  @include mediaQueries.screen-xxl {
    width: 15%;
  }
}
</style>
