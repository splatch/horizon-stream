<template>
  <ul class="cards">
    <li v-for="node in nodes" :key="node?.id">
      <section class="header">
        <Icon :icon="storage" data-test="icon-storage" />
        <h4 data-test="heading">{{ node?.label }}</h4>
      </section>
      <section class="content">
        <div>
          <FeatherChipList label="List of metric chips" data-test="metric-chip-list">
            <MetricChip v-for="metric in node?.metrics" :key="metric?.type" :metric="metric" />
          </FeatherChipList>
          <InventoryTextAnchorList :anchor="node?.anchor" data-test="text-anchor-list" />
        </div>
        <InventoryIconActionList :node="node" class="icon-action" data-test="icon-action-list" />
      </section>
      <div class="overlay" v-if="node.isEditMode">
        <feather-checkbox id="tagged" v-model="node.isTaggingChecked" @update:model-value="editNodeTags(node.id)" class="tag-node-checkbox"/>
        <section class="overlay-header">
          <Icon :icon="storage" data-test="icon-storage" />
          <h4 data-test="heading">{{ node?.label }}</h4>
        </section>
        <section class="overlay-content">
          <div class="title"><label for="iconCheckbox">Tagged</label><Icon :icon="checkbox" data-test="icon-checkbox" /></div>
          <FeatherChipList condensed label="Tag list">
            <FeatherChip v-for="tag in tagsSelected" :key="tag">{{ tag }}</FeatherChip>
          </FeatherChipList>
        </section>
      </div>
    </li>
  </ul>
  <PrimaryModal :visible="isVisible" :title="modal.title" :class="modal.cssClass">
    <template #content>
      <p>{{ modal.content }}</p>
    </template>
    <template #footer>
      <FeatherButton 
        data-testid="cancel-btn" 
        secondary 
        @click="cancelTagsAllNodes">
          {{ modal.cancelLabel }}
      </FeatherButton>
      <FeatherButton 
        data-testid="save-btn" 
        primary
        @click="saveTagsAllNodes">
          {{ modal.saveLabel }}
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import Storage from '@material-design-icons/svg/outlined/storage.svg'
import Checkbox from '@material-design-icons/svg/outlined/check_box.svg'
import { NodeContent } from '@/types/inventory'
import { IIcon } from '@/types'
import { useTaggingStore } from '@/store/Components/taggingStore'
import { useNodeMutations } from '@/store/Mutations/nodeMutations'
import { TagNodesType } from '@/types/tags'
import { ModalPrimary } from '@/types/modal'
import useModal from '@/composables/useModal'

// TODO: sort tabContent alpha (default)
const props = defineProps({
  tabContent: {
    type: Object as PropType<NodeContent[]>,
    required: true
  }
})
const nodes = ref<NodeContent[]>(props.tabContent)

const { openModal, closeModal, isVisible } = useModal()

const taggingStore = useTaggingStore()
const nodeMutations= useNodeMutations()

const tagsSelected = computed(() => taggingStore.selectedTags)
const tagNodesSelected = computed(() => taggingStore.tagNodesSelected)

watch(tagNodesSelected, (selected) => {
  let isTaggingChecked = false
  let isEditMode = false

  if(selected === TagNodesType.All) {
    isTaggingChecked = true
    isEditMode = true
    openModal()
  } else if(selected === TagNodesType.Individual) {
    isTaggingChecked = false
    isEditMode = true
  }

  nodes.value = nodes.value.map(node => ({
    ...node,
    isTaggingChecked,
    isEditMode
  }))
})

const cancelTagsAllNodes = () => {
  taggingStore.selectTagNodes(TagNodesType.Unselected)
  closeModal()
}

const saveTagsAllNodes = () => {
  taggingStore.selectTagNodes(TagNodesType.Unselected)
  nodeMutations.addTagsToAllNodes()
  // refetch nodes
  // refetch tags
  closeModal()
}

/**
  * Tagging individual node
  * - check/uncheck node: query to add/remove tags
  *   - display toaster
  *   - select clear to exit tagging mode
  *     - node checkbox: set checked to false
  * @param id the node to be edited (add/remove tags)
  */
const editNodeTags = (id: number) => {
  const toAddTags = nodes.value.filter((node) => node.id === id)[0].isTaggingChecked || false

  nodeMutations.editTagsToNode(id, toAddTags) // node id and boolean for add or remove tags
}

const modal: ModalPrimary = {
  title: 'Add tags to all nodes?',
  cssClass: '',
  content: '',
  id: '',
  cancelLabel: 'cancel',
  saveLabel: 'ok',
  hideTitle: true
}

const storage: IIcon = {
  image: Storage,
  title: 'Node'
}
const checkbox: IIcon = {
  image: Checkbox,
  title: ''
}
</script>

<style lang="scss" scoped>
@use "@featherds/styles/themes/variables";
@use "@/styles/vars";
@use "@/styles/mediaQueriesMixins";

.overlay {
  $color-header-title: white;

  position: absolute;
  lefT: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(10, 12, 27, 0.75); // darker than shade-1
  padding: var(variables.$spacing-l) var(variables.$spacing-l);
  border-radius: 0 vars.$border-radius-m vars.$border-radius-m 0;
  .tag-node-checkbox {
    position: absolute;
    top: 0;
    right: 0;
    :deep {
      label {
        display: none;
      }
      .feather-checkbox {
        .box {
          border-color: $color-header-title;
        }
        &[aria-checked=true] {
          .box {
            border-color: var(variables.$primary);
          }
        }
      }
    }
  }
  > .overlay-header {
    margin-bottom: var(variables.$spacing-xs);
    margin-right: var(variables.$spacing-s);
    display: flex;
    flex-direction: row;
    gap: 0.5rem;
    align-items: center;
    color: $color-header-title;
    > h4 {
      color: $color-header-title;
    }
  }
  > .overlay-content {
    > .title {
      display: flex;
      flex-direction: row;
      align-items: center;
      color: $color-header-title;
    }
    label {
      font-size: 1.5rem;
      font-weight: bold;
    }
    :deep(svg) {
      width: 3em;
      height: 3em;
      margin-left: var(variables.$spacing-xs);
    }
    .chip-list {
      margin-top: var(variables.$spacing-s);
      :deep {
        .chip {
          margin: 0;
          background-color: var(variables.$primary);
          .label {
            color: var(variables.$primary-text-on-color);
          }
        }
      }
    }
  }
}

ul.cards {
  display: flex;
  flex-flow: row wrap;
  gap: 1rem;
  > li {
    position: relative;
    padding: var(variables.$spacing-l) var(variables.$spacing-l);
    border: 1px solid var(variables.$secondary-text-on-surface); 
    border-radius: 10px;
    border-left: 10px solid var(variables.$secondary-text-on-surface); // TODO set color dynamically to the node's status
    min-width: 400px;

    @include mediaQueriesMixins.screen-sm {
      width: 100%;
    }
    @include mediaQueriesMixins.screen-md {
      max-width: 480px;
      width: 100%;
    }
    @include mediaQueriesMixins.screen-lg {
      width: 48%;
    }
    @include mediaQueriesMixins.screen-xl {
      width: 32%;
    }
    @include mediaQueriesMixins.screen-xxl {
      width: 24%;
    }
    
    > .header {
      margin-bottom: var(variables.$spacing-s);
      display: flex;
      flex-direction: row;
      gap: 0.5rem;
      align-items: center;
      > h4 {
        color: var(variables.$primary);
      }
    }
  }
  .content {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    gap: 2rem;
  }
}

.chip-list {
  margin: 0 0 var(variables.$spacing-s);
  gap: 0.5rem;
}
</style>