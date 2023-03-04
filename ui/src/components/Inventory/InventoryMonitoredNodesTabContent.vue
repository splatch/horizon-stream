<template>
  <div class="ctrls">
    <FeatherButton
      data-test="toggle-select-all-btn"
      pimary
      @click="toggleSelectAllNodes"
    >
      {{ areAllNodesSelected ? 'Deselect all' : 'Select all' }}
    </FeatherButton>
    <FeatherButton
      data-test="open-modal-btn"
      primary
      @click="openModalSaveTags"
    >
      Save
    </FeatherButton>
  </div>
  <ul class="cards">
    <li
      v-for="node in nodes"
      :key="node?.id"
    >
      <section class="header">
        <Icon
          :icon="storageIcon"
          data-test="icon-storage"
        />
        <h4 data-test="heading">{{ node?.label }}</h4>
      </section>
      <section class="content">
        <div>
          <FeatherChipList
            label="List of metric chips"
            data-test="metric-chip-list"
          >
            <MetricChip
              v-for="metric in node?.metrics"
              :key="metric?.type"
              :metric="metric"
            />
          </FeatherChipList>
          <InventoryTextAnchorList
            :anchor="node?.anchor"
            data-test="text-anchor-list"
          />
        </div>
        <InventoryIconActionList
          :node="node"
          class="icon-action"
          data-test="icon-action-list"
        />
      </section>
      <InventoryNodeTagEditOverlay
        v-if="isNodeEditMode"
        :node="node"
      />
    </li>
  </ul>
  <PrimaryModal
    :visible="isVisible"
    :title="modal.title"
    :class="modal.cssClass"
  >
    <template #content>
      <p>{{ modal.content }}</p>
      <FeatherChipList
        condensed
        label="Tag list"
      >
        <FeatherChip
          v-for="tag in tagsSelected"
          :key="tag.id"
          >{{ tag.name }}</FeatherChip
        >
      </FeatherChipList>
    </template>
    <template #footer>
      <FeatherButton
        data-testid="cancel-btn"
        secondary
        @click="closeModal"
      >
        {{ modal.cancelLabel }}
      </FeatherButton>
      <FeatherButton
        data-testid="save-btn"
        primary
        @click="saveTagsToSelectedNodes"
      >
        {{ modal.saveLabel }}
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script lang="ts" setup>
import { PropType } from 'vue'
import Storage from '@material-design-icons/svg/outlined/storage.svg'
import { NodeContent } from '@/types/inventory'
import { IIcon } from '@/types'
import { useTagStore } from '@/store/Components/tagStore'
import { useNodeMutations } from '@/store/Mutations/nodeMutations'
import { useInventoryQueries } from '@/store/Queries/inventoryQueries'
import { useInventoryStore } from '@/store/Views/inventoryStore'
import { ModalPrimary } from '@/types/modal'
import useModal from '@/composables/useModal'

// TODO: sort tabContent alpha (default)? to keep list display consistently in same order (e.g. page refresh)
const props = defineProps({
  tabContent: {
    type: Object as PropType<NodeContent[]>,
    required: true
  }
})
const nodes = ref<NodeContent[]>(props.tabContent)

const { openModal, closeModal, isVisible } = useModal()

const tagStore = useTagStore()
const nodeMutations = useNodeMutations()
const inventoryQueries = useInventoryQueries()
const inventoryStore = useInventoryStore()

const nodeSelected = computed(() => inventoryStore.nodeSelected)
const tagsSelected = computed(() => tagStore.tagsSelected)

const isNodeEditMode = computed(() => tagStore.isTagEditMode)

const areAllNodesSelected = ref(false)
const toggleSelectAllNodes = () => (areAllNodesSelected.value = !areAllNodesSelected.value)

watch(areAllNodesSelected, (isAllSelected) => {
  let isNodeOverlayChecked = false

  if (isAllSelected) {
    isNodeOverlayChecked = true

    nodes.value.forEach((node) => {
      inventoryStore.setNodeSelection(node, true)
    })
  }

  nodes.value = nodes.value.map((node) => ({
    ...node,
    isNodeOverlayChecked
  }))
  console.log(nodes.value)
})

const openModalSaveTags = () => {
  const selectedNodesLabels = nodeSelected.value.map(({ label }) => label)
  const modalContent = areAllNodesSelected.value ? 'Add tags to all nodes?' : `Add tags to ${selectedNodesLabels}`
  modal.value = {
    ...modal.value,
    content: modalContent
  }

  openModal()
}

const saveTagsToSelectedNodes = () => {
  // call inventoryMutatons
  inventoryStore.nodeSelected.forEach(({ id }) => {
    nodeMutations.addTagsToNode({ nodeId: id, tags: tagStore.tagsSelected })
  })

  inventoryQueries.fetch()
  tagStore.selectAllTags(false)
  closeModal()
}

const modal = ref<ModalPrimary>({
  title: '',
  cssClass: '',
  content: '',
  id: '',
  cancelLabel: 'cancel',
  saveLabel: 'ok',
  hideTitle: true
})

const storageIcon: IIcon = {
  image: Storage,
  title: 'Node',
  size: 1.5
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/vars';
@use '@/styles/mediaQueriesMixins';

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
