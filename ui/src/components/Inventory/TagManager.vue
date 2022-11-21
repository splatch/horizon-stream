<template>
  <div class="tag-manager-box">
    <div class="left">
      <div class="top">
        <div class="subtitle">Selected Tags: </div>
        <div class="subtitle">Total: </div>
        <div class="subtitle">|</div>
        <div class="subtitle">Selected: {{ selectedTags.length }}</div>

        <div class="search-add">
          <!-- Add tag -->
          <FeatherButton icon="Add Tag">
            <FeatherIcon :icon="addIcon" />
          </FeatherButton>

          <!-- Search tags input -->
          <FeatherInput
            class="search"
            v-model="searchValue"
            label="Search Tags">
            <template v-slot:pre>
              <FeatherIcon :icon="searchIcon" />
            </template>
          </FeatherInput>
        </div>

      </div>
      <div class="chips">
        <FeatherChipList condensed label="Tags" :key="selectedTags.toString()">
          <FeatherChip 
            v-for="tag of tags" 
            :key="tag" 
            class="pointer"
            :class="{ 'selected' : selectedTags.includes(tag) }"
            @click="selectTag(tag)"
          >
            {{ tag }}
          </FeatherChip>
        </FeatherChipList>
      </div>
    </div>

    <div class="right">
      <div class="vl"></div>
      <FeatherRadioGroup vertical :label="'Tag Nodes:'" v-model="tagNodes" class="radio-btns">
        <FeatherRadio :value="1">All</FeatherRadio>
        <FeatherRadio :value="2">Individual</FeatherRadio>
        <FeatherRadio :value="3">Clear</FeatherRadio>
      </FeatherRadioGroup>
    </div>
  </div>
</template>

<script setup lang="ts">
import Search from '@featherds/icon/action/Search'
import AddIcon from "@featherds/icon/action/AddCircleAlt"

const searchIcon = markRaw(Search)
const addIcon = markRaw(AddIcon)
const tagNodes = ref()
const searchValue = ref()
const selectedTags = ref<string[]>([])

const tags = computed(() => ['tag1', 'tag2'])
// import KeyboardArrowDown from '@featherds/icon/navigation/ExpandMore'
// const downIcon = markRaw(KeyboardArrowDown)

const selectTag = (tag: string) => {
  if (selectedTags.value.includes(tag)) {
    selectedTags.value = selectedTags.value.filter(t => t !== tag)
  } else {
    selectedTags.value.push(tag)
  }
}
</script>

<style scoped lang="scss">
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/typography";
.subtitle {
  @include typography.subtitle2();
}
.tag-manager-box {
  display: flex;
  height: 175px;
  background: var(variables.$shade-4);
  margin: 10px 20px 10px 20px;
  border-radius: 3px;
  padding: 15px;

  .left {
    width: 80%;
    display: flex;
    flex-direction: column;
    padding-right: 15px;

    .top {
      display: flex;
      justify-content: space-between;

      .search-add {
        display: flex;
        gap: 5px;
        .search {
          width: 200px;
        }
      }
    }

    .chips {

    }
  }

  .right {
    display: flex;

    .vl {
      border-left: 1px solid var(variables.$shade-3);
      height: 135px;
      margin-top: 6px;
      margin-right: 20px;
    }

    .radio-btns {
      // margin-top: 15px;
    }
  }
}
</style>


<style lang="scss">
.tag-manager-box {
  .feather-radio-group-container.vertical .layout-container {
    margin-bottom: 0px !important;
  }
}
</style>