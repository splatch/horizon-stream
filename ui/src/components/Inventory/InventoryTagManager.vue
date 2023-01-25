<template>
  <div class="tag-manager-box" v-if="isTaggingBoxOpen">
    <section class="select-tags">
      <div class="top">
        <div class="heading-total-selected">
          <h4>Select Tags:</h4>
          <div class="total-selected">
            <div>TOTAL: <span>{{ tags.length }}</span><span class="pipe">|</span></div>
            <div>SELECTED: <span>{{ selectedTags.length }}</span></div>
          </div>
        </div>
        <div class="search-add">
          <!-- Add tag -->
          <InputButtonPopover :handler="addTag" label="Add Tag" />
          <!-- Search tags input -->
          <FeatherInput
            class="search"
            v-model="searchValue"
            label="Search Tags">
            <template v-slot:post>
              <Icon :icon="searchIcon" class="icon-search" />
            </template>
          </FeatherInput>
        </div>
      </div>
      <FeatherChipList condensed label="Tags" :key="selectedTags.toString()">
        <FeatherChip 
          v-for="tag of tags" 
          :key="tag" 
          class="pointer"
          :class="{ 'selected' : selectedTags.includes(tag) }"
          @click="taggingStore.toggleTag(tag)"
        >
          {{ tag }}
        </FeatherChip>
      </FeatherChipList>
    </section>
    <section class="tag-nodes">
      <h4>Tag Nodes:</h4>
      <FeatherRadioGroup vertical label="" v-model="taggingStore.tagNodesSelected" class="select-tag-nodes">
        <FeatherRadio :value="TagNodesType.All">All</FeatherRadio>
        <FeatherRadio :value="TagNodesType.Individual">Individual</FeatherRadio>
        <FeatherRadio :value="TagNodesType.Clear">Clear</FeatherRadio>
      </FeatherRadioGroup>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useInventoryStore } from '@/store/Views/inventoryStore'
import { useTaggingQueries } from '@/store/Queries/taggingQueries'
import { useTaggingStore } from '@/store/Components/taggingStore'
import { useTaggingMutations } from '@/store/Mutations/taggingMutations'
import Search from '@featherds/icon/action/Search'
import { IIcon } from '@/types'
import { TagNodesType } from '@/types/tags'

const inventoryStore = useInventoryStore()
const taggingQueries = useTaggingQueries()
const taggingStore = useTaggingStore()
const taggingMutations = useTaggingMutations()

const searchIcon: IIcon = {
  image: markRaw(Search),
  tooltip: 'Search'
}

const searchValue = ref()
const tags = computed(() => taggingQueries.tags)
const selectedTags = computed(() => taggingStore.selectedTags)

const isTaggingBoxOpen = computed(() => {
  if(!inventoryStore.isTaggingBoxOpen) {
    taggingQueries.resetTags()
  } else {
    taggingQueries.fetchTags()
  }

  return inventoryStore.isTaggingBoxOpen
})

const addTag = (val: string) => {
  taggingMutations.editTag(val, true)
}
</script>

<style scoped lang="scss">
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";
@use "@/styles/vars";
@use "@/styles/mediaQueries";

.tag-manager-box {
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
      .total-selected {
        display: flex;
        flex-direction: row;
        padding-top: 8px;
        > * {
          > span {
            font-weight: bold;
            &.pipe {
              color: var(variables.$secondary-text-on-surface);
              margin: 0 var(variables.$spacing-s);
            }
          }
        }
      }

      @include mediaQueries.screen-md {
        display: flex;
        flex-direction: row;
        width: 50%;
        justify-content: space-between;
      }
    }
    .search-add {
      display: flex;
      flex-direction: row;
      .add-tag-dropdown {
        .new-tag-input {
          margin: 0px 10px 0px 10px;
          width: 175px;
        }
        .new-tag-btn {
          margin-left: var(variables.$spacing-s);
        }
        .feather-menu-dropdown {
          color: red;
        }
      }

      :deep(.search) {
        width: 200px;
      }
    }

    @include mediaQueries.screen-md {
      margin-bottom: 0;
    }
  }
  .tag-chip-list {
    margin-top: 0;
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
  width: 100%;;
  min-width: 445px;
  margin-top: var(variables.$spacing-m);
  padding-top: var(variables.$spacing-m);
  border-top: 1px solid var(variables.$secondary-text-on-surface);

  :deep(.select-tag-nodes) {
    margin-left: var(variables.$spacing-m);
    > label {
      display: none;
    }
    .feather-radio-group {
      display: flex;
      flex-direction: row;
      .layout-container {
        margin-right: var(variables.$spacing-m);
      }
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

    :deep(.select-tag-nodes) {
      .feather-radio-group {
        display: block;
        > * {
          margin-bottom: var(variables.$spacing-xs);
        }
      }
    }
  }

  @include mediaQueries.screen-xxl {
    width: 15%;
  }
}
</style>