<script setup lang="ts">
import { useInventoryStore } from '@/store/Views/inventoryStore'
import Search from '@featherds/icon/action/Search'
import AddIcon from '@featherds/icon/action/AddCircleAlt'

const inventoryStore = useInventoryStore()

const searchIcon = markRaw(Search)
const addIcon = markRaw(AddIcon)

const newTag = ref()
const newTagDropdown = ref()
const tagNodes = ref()
const searchValue = ref()
const selectedTags = ref<string[]>([])
const tagSelected = ref<Record<string, boolean>>({})

const tags = computed(() => [
  'tag1tag1 tag1tag1tag1tag1tag1tag1', 'tag2', 'tag3', 'tag4', 'tag5', 'tag6', 'tag7', 'tag8', 'tag9', 'tag10', 'tag11', 'tag12', 'tag13', 'tag14', 'tag15', 'tag16', 'tag17', 'tag18', 'tag19', 'tag20', 'tag21', 'tag22'
])

const selectTag = (tag: string) => {
  if (selectedTags.value.includes(tag)) {
    selectedTags.value = selectedTags.value.filter(t => t !== tag)
  } else {
    selectedTags.value.push(tag)
  }
}

const addTag = () => {
  // send newtag.value
  newTag.value = '' // clear input
  newTagDropdown.value.handleClose() // close dropdown
}
</script>

<template>
  <div class="tag-manager-box" v-if="inventoryStore.isTaggingBoxOpen">
    <section class="select-tags">
      <div class="top">
        <h4>Select Tags:</h4>
        <FeatherInput
          class="search"
          v-model="searchValue"
          label="Search Tags">
          <template v-slot:post>
            <FeatherIcon :icon="searchIcon" />
          </template>
        </FeatherInput>
      </div>
      <FeatherList class="tag-list">
        <FeatherListItem v-for="tag in tags" :key="tag" asLi>
          <FeatherCheckbox :id="tag" v-model="tagSelected[tag]" @update:model-value="selectTag(tag)"
            >{{ tag }}
          </FeatherCheckbox>
        </FeatherListItem>
      </FeatherList>
    </section>
    <section class="selected-tags">
      <h4>Selected Tags:</h4>
      <FeatherList class="tag-list">
        <FeatherListItem v-for="tag in selectedTags" :key="tag" asLi
          >{{ tag }}
        </FeatherListItem>
      </FeatherList>
    </section>
    <section class="tag-nodes">
      <h4>Tag Nodes:</h4>
      <FeatherRadioGroup vertical label="" v-model="tagNodes" class="select-tag-nodes">
        <FeatherRadio :value="1">All</FeatherRadio>
        <FeatherRadio :value="2">Individual</FeatherRadio>
        <FeatherRadio :value="3">Clear</FeatherRadio>
      </FeatherRadioGroup>
    </section>
  </div>
</template>

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
    :deep(.search) {
      width: 200px;
    }
  }
  :deep(.tag-list) {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    > li {
      label {
        width: 100px;
        overflow: scroll;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }

  @include mediaQueries.screen-lg {
    width: 45%;
    min-width: 0;
  }
  @include mediaQueries.screen-xxl {
    width: 50%;
  }
}
.selected-tags {
  width: 100%;
  min-width: 445px;
  margin-top: var(variables.$spacing-m);
  padding-top: var(variables.$spacing-m);
  border-top: 1px solid var(variables.$secondary-text-on-surface);
  :deep(.tag-list) {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    > li {
      cursor: default;
      &:hover {
        background: none;
      }
      > span {
        width: 80px;
        overflow: scroll;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }

  @include mediaQueries.screen-lg {
    width: 35%;
    min-width: 0;
    margin-top: 0;
    padding-top: 0;
    padding-left: var(variables.$spacing-m);
    border-top: 0;
    border-left: 1px solid var(variables.$secondary-text-on-surface);
    h4 {
      margin-bottom: 26px;
    }
  }
  @include mediaQueries.screen-xxl {
    width: 30%;
  }
}
.tag-nodes {
  display: flex;
  flex-direction: column;
  width: 100%;;
  min-width: 445px;
  margin-top: var(variables.$spacing-m);
  padding-top: var(variables.$spacing-m);
  border-top: 1px solid var(variables.$secondary-text-on-surface);
  :deep(.select-tag-nodes) {
    margin-left: var(variables.$spacing-m);
    .feather-input-sub-text {
      display: none;
    }
  }

  @include mediaQueries.screen-lg {
    width: 15%;
    min-width: 0;
    margin-top: 0;
    padding-top: 0;
    padding-left: var(variables.$spacing-m);
    border-top: 0;
    border-left: 1px solid var(variables.$secondary-text-on-surface);
    h4 {
      margin-bottom: 20px;
    }
  }
  @include mediaQueries.screen-xxl {
    width: 15%;
  }
}
</style>