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
          <FeatherPopover :pointer-alignment="alignment" :placement="placement">
            <template #default>
              <div class="add-new-tag-popover">
                <FeatherInput
                  :label="'type...'"
                  v-model="newTag"
                  class="new-tag-input"
                />
                <FeatherButton @click="addTag" primary>Add tag</FeatherButton>
              </div>
            </template>
            <template #trigger="{ attrs, on }">
              <FeatherButton v-bind="attrs" v-on="on" class="add-new-tag-btn"
                >
                <Icon :icon="addIcon" />
              </FeatherButton
              >
            </template>
          </FeatherPopover>
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
      <FeatherChipList condensed label="Tags" class="tag-chip-list">
        <FeatherChip 
          v-for="tag of tags" 
          :key="tag.id" 
          class="pointer"
          :class="{ 'selected' : selectedTags.id === tag.id }"
          @click="selectTag(tag)"
          >
          {{ tag.label }}
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
import Search from '@featherds/icon/action/Search'
import Add from '@featherds/icon/action/Add'
import { IIcon } from '@/types'
import { PointerAlignment, PopoverPlacement } from '@featherds/popover'
import { TagNodesType, Tag } from '@/types/tags'

const inventoryStore = useInventoryStore()
const taggingQueries = useTaggingQueries()
const taggingStore = useTaggingStore()

const searchIcon: IIcon = {
  image: markRaw(Search),
  tooltip: 'Search'
}

const addIcon: IIcon = {
  image: markRaw(Add),
  size: '2rem'
}

const newTag = ref()
const newTagDropdown = ref()
const searchValue = ref()
const selectedTags = ref<Tag[]>([])

const isTaggingBoxOpen = computed(() => {
  if(!inventoryStore.isTaggingBoxOpen) {
    taggingQueries.resetTags()
  } else {
    taggingQueries.fetchTags()
  }

  return inventoryStore.isTaggingBoxOpen
})

const tags = computed(() => taggingQueries.tags)

const selectTag = (tag: Tag) => {
  if (selectedTags.value.includes(tag)) {
    selectedTags.value = selectedTags.value.filter(t => t !== tag)
  } else {
    selectedTags.value.push(tag)
  }
}

const placement = ref(PopoverPlacement.top)
const alignment = ref(PointerAlignment.center)

const addTag = () => {
  // send newtag.value
  newTag.value = '' // clear input
  newTagDropdown.value.handleClose() // close dropdown
}
</script>

<style scoped lang="scss">
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";
@use "@/styles/vars";
@use "@/styles/mediaQueries";


:deep(.feather-popover-container) {
  > .popover {
    // to style add tag popover
  }
}
.add-new-tag-btn {
  color: white;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  min-width: inherit;
  padding: 1rem;
  line-height: inherit;
  background-color: var(variables.$shade-2);
  // TODO: how to remove the weird blue border when button clicked
  :deep {
    > .btn-content {
      display: block;
      > svg {
        left: -1rem; // TODO: how to set dynamically using addIcon.size value
        top: -1rem;
      }
    }
  }
}

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