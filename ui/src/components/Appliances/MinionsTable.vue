<template>
  <TableCard v-if="appliancesStore.minionsTableOpen">
    <div class="header">
      <div class="title">
        Minions ({{ applianceQueries.tableMinions.length }})
      </div>
      <FeatherButton 
        data-test="hide-minions-btn"
        icon="Hide Minions" 
        @click="appliancesStore.hideMinionsTable"
        v-if="!widgetProps?.isWidget"
      >
        <FeatherIcon :icon="ChevronLeft" />
      </FeatherButton>
    </div>
    <div class="container">
      <table class="tl1 tl2 tl3 tc4 tc5 tc6 data-table" aria-label="Minions Table" data-test="minions-table">
        <thead>
          <tr>
            <th scope="col" data-test="col-label">Label</th>
            <th scope="col" data-test="col-date">Time</th>
            <th scope="col" data-test="col-minion">Id</th>
            <th scope="col" data-test="col-latency">Latency</th>
            <th scope="col" data-test="col-status">Status</th>
            <th scope="col" data-test="col-delete">Delete</th>
          </tr>
        </thead>
        <TransitionGroup name="data-table" tag="tbody">
          <tr v-for="(minion, index) in minionsTable" :key="(minion.id as string)" :data-index="index" data-test="minion-item">
            <td>{{ minion.label }}</td>
            <td v-date>{{ minion.lastCheckedTime }}</td>
            <td>{{ minion.id }}</td>
            <MetricChip tag="td" :metric="{timestamp: minion.latency?.timestamp}" :data-metric="minion.latency?.timestamp" class="bg-status" data-test="minion-item-latency" />
            <MetricChip tag="td" :metric="{status: minion.status}" class="bg-status" data-test="minion-item-status" />
            <td>
              <FeatherButton
                data-test="minion-item-delete-btn"
                icon="Delete"
                @click="deleteMinion(minion.id, minion.label)"
              >
                <FeatherIcon :icon="deleteIcon" />
              </FeatherButton>
            </td>
          </tr>
        </TransitionGroup>
      </table>
    </div>
  </TableCard>
  <PrimaryModal :visible="isVisible" :title="modal.title" :class="modal.cssClass">
    <template #content>
      <div v-html="modal.content"></div>
      <!-- <LineGraph :graph="graphProps" /> -->
    </template>
    <template #footer>
      <FeatherButton 
        data-testid="cancel-btn" 
        secondary 
        @click="modal.action.cancel.handler">
          {{ modal.action.cancel.label }}
      </FeatherButton>
      <FeatherButton 
        data-testid="save-btn" 
        primary
        @click="modal.action.save.handler">
          {{ modal.action.save.label }}
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script setup lang="ts">
import { useAppliancesQueries } from '@/store/Queries/appliancesQueries'
// import { useMinionMutations } from '@/store/Mutations/minionMutations'
import { useAppliancesStore } from '@/store/Views/appliancesStore'
import ChevronLeft from '@featherds/icon/navigation/ChevronLeft'
import Delete from '@featherds/icon/action/Delete'
import { Monitor, WidgetProps, ModalAction} from '@/types'
import { GraphProps } from '@/types/graphs'
import { ExtendedMinion } from '@/types/minion'
import { TimeRangeUnit } from '@/types/graphql'
import MetricChip from '../Common/MetricChip.vue'
import useSnackbar from '@/composables/useSnackbar'
import useModal from '@/composables/useModal'

// import mockQuery from '@/mocking/graphql/sample'
// import { useSampleMock } from '@/mocking/graphql/query.ts.bk'




defineProps<{widgetProps?: WidgetProps}>()

const { showSnackbar } = useSnackbar()
const { openModal, closeModal, isVisible } = useModal()

const deleteIcon = markRaw(Delete)

const graphProps = ref({} as GraphProps)

const appliancesStore = useAppliancesStore()
const applianceQueries = useAppliancesQueries()
// const minionMutations = useMinionMutations()
const minionsTable = computed<ExtendedMinion[]>(() => applianceQueries.tableMinions)



// const sampleMock = useSampleMock()


// const mockQuery = await sampleMock.query

// console.log(mockQuery)


 

const modal = ref({
  isVisible: false,
  title: '',
  cssClass: '',
  content: '',
  action: {
    cancel: <ModalAction>{},
    save: <ModalAction>{}
  },
  hideTitle: true
})


const deleteMinionHandler = async (id: number) => {
  // const deleteMinion = await minionMutations.deleteMinion(id)

  // TODO: remove this once BE avail.
  setTimeout(() => {
    closeModal()
    showSnackbar({ msg: 'Minion successfully deleted.' })
  }, 2000)

  /* if (!deleteMinion.error) {
    // clears node obj on successful save
    // Object.assign(node, defaultDevice)

    closeModal()

    showSnackbar({
      msg: 'Minion successfully deleted.'
    })

    // Timeout because minion may not be available right away
    // TODO: Replace timeout with websocket/polling
    setTimeout(() => {
      applianceQueries.fetchMinionsForTable()
    }, 350)
  } */
}

const deleteMinion = (id: number, label: string | undefined) => {
  modal.value = {
    ...modal.value,
    title: label || '',
    cssClass: 'minion-delete-modal',
    content: `
      <p>Are you sure to delete</p>
    `,
    action: {
      cancel: {
        label: 'Cancel',
        handler: closeModal
      },
      save: {
        label: 'Delete',
        handler: deleteMinionHandler(id)
      }
    }
  }

  openModal()
}


const openLatencyGraph = (minion: ExtendedMinion) => {
  modal.value = {
    ...modal.value,
    isVisible: true
  }
  graphProps.value = {
    label: 'Minion Latency',
    metrics: ['response_time_msec'],
    monitor: Monitor.ECHO,
    nodeId: minion.id,
    instance: minion.systemId as string, // for minions, can use systemId for instance
    timeRange: 10,
    timeRangeUnit: TimeRangeUnit.Minute
  }
}
</script>

<style lang="scss" scoped>
@use "@featherds/styles/mixins/typography";
@use "@featherds/table/scss/table";
@use "@/styles/_statusBackground";

.header {
  display: flex;
  justify-content: space-between;
  .title {
    @include typography.headline3;
    margin-left: 15px;
  }
}

.container {
  display: block;
  overflow-x: auto;

  table {
    width: 100%;
    @include table.table;
    @include table.table-condensed;
    thead {
      background: var(typography.$background);
      text-transform: uppercase;
    }
    td {
      white-space: nowrap;
      display: table-cell;
      div {
        border-radius: 5px;
        padding: 0px 5px 0px 5px;
      }
    }
  }
}
</style>
