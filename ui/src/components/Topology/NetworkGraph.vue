<template>
  <div class="tooltip-wrapper">
    <VNetworkGraph
      ref="graph"
      v-model:selectedNodes="selectedNodes"
      :layouts="layout"
      :nodes="vertices"
      :edges="edges"
      :configs="configs"
      :zoomLevel="zoomLevel"
      :eventHandlers="eventHandlers"
      v-if="trigger && focusObjects.length !== 0"
    >
      <defs>
        <!--
        Define the path for clipping the image.
        To change the size of the applied node as it changes,
        add the `clipPathUnits="objectBoundingBox"` attribute
        and specify the relative size (0.0~1.0).
        -->
        <clipPath
          id="iconCircle"
          clipPathUnits="objectBoundingBox"
        >
          <circle
            cx="0.5"
            cy="0.5"
            r="0.5"
          />
        </clipPath>
      </defs>

      <!-- Replace the node component -->
      <template #override-node="{ nodeId, scale, config, ...slotProps }">
        <!--
        The base position of the <image /> is top left. The node's
        center should be (0,0), so slide it by specifying x and y.
        -->
        <image
          v-if="ICON_PATHS[vertices[nodeId].icon]"
          :class="{ 'unfocused': highlightFocusedObjects && !vertices[nodeId].focused }"
          class="node-icon pointer"
          :x="-config.radius * scale"
          :y="-config.radius * scale"
          :width="config.radius * scale * 2"
          :height="config.radius * scale * 2"
          :xlink:href="ICON_PATHS[vertices[nodeId].icon]"
          clip-path="url(#iconCircle)"
        />

        <!-- circle for drawing stroke -->
        <circle
          v-if="vertices[nodeId].subLayer"
          class="node-circle"
          :cx="-12 * scale"
          :cy="12 * scale"
          :r="5 * scale"
          :fill="setColor(vertices[nodeId])"
          v-bind="slotProps"
        />
      </template>
    </VNetworkGraph>
    <!-- Tooltip -->
    <div
      ref="tooltip"
      class="tooltip"
      :style="{ ...tooltipPos }"
      v-if="vertices[targetNodeId] && vertices[targetNodeId].tooltip"
    >
      {{vertices[targetNodeId].tooltip}}
    </div>
  </div>
  <NoFocusMsg v-if="!focusObjects.length" />
  <TopologyModal
    :nodeId="contextNode.id"
    v-if="contextNode && contextNode.id"
  />
  <ContextMenu
    ref="contextMenu"
    v-if="showContextMenu"
    :refresh="refresh"
    :contextMenuType="contextMenuType"
    :x="menuXPos"
    :y="menuYPos"
    :node="contextNode"
    :selectedNodeObjects="selectedNodeObjects"
    :selectedNodes="selectedNodes"
    :groupClick="groupClick"
    :closeContextMenu="closeContextMenu"
  />
</template>

<script
  setup
  lang="ts"
>
import 'v-network-graph/lib/style.css'
import { useTopologyStore } from '@/store/Views/topologyStore'
import { VNetworkGraph, defineConfigs, Layouts, Edges, Nodes, SimpleLayout, EventHandlers, NodeEvent, Instance, ViewEvent, Node, Edge } from 'v-network-graph'
import { ForceLayout, ForceNodeDatum, ForceEdgeDatum } from 'v-network-graph/lib/force-layout'
import ContextMenu from './ContextMenu.vue'
import NoFocusMsg from './NoFocusMsg.vue'
import { onClickOutside } from '@vueuse/core'
import { SimulationNodeDatum } from 'd3'
import { ContextMenuType, ViewType } from './topology.constants'
import TopologyModal from './TopologyModal.vue'
import ICON_PATHS from './icons/iconPaths'
import { IdLabelProps } from '@/types'

interface D3Node extends Required<SimulationNodeDatum> {
  id: string
}

defineProps({
  refresh: {
    type: Function,
    required: true
  }
})

const topologyStore = useTopologyStore()
const zoomLevel = ref(1)
const graph = ref<Instance>()
const selectedNodes = ref<string[]>([]) // string ids
const selectedNodeObjects = ref<Node[]>([]) // full nodes
const tooltip = ref<HTMLDivElement>()
const showTooltip = ref(false)
const cancelTooltipDebounce = ref(false)
const targetNodeId = ref('')
const d3Nodes = ref<D3Node[]>([])
const contextMenu = ref(null)
const showContextMenu = ref(false)
const contextNode = ref()
const contextMenuType = ref()
const menuXPos = ref(0)
const menuYPos = ref(0)
const groupClick = ref(false)

const getD3NodeCoords = () => d3Nodes.value.filter((d3Node) => d3Node.id === targetNodeId.value).map((d3Node) => ({ x: d3Node.x, y: d3Node.y }))[0]


const displayContextMenu = (x: number, y: number) => {
  menuXPos.value = x
  menuYPos.value = y
  showContextMenu.value = true
}
const closeContextMenu = () => showContextMenu.value = false
onClickOutside(contextMenu, () => closeContextMenu())


const displayTooltip = (show = false) => {
  if (!show) { // hide
    cancelTooltipDebounce.value = true
    showTooltip.value= false
  } else { // show
    cancelTooltipDebounce.value = false

    const tooltip = useDebounceFn(() => {
      if (!cancelTooltipDebounce.value) {
        showTooltip.value = true
      }
    }, 1000)

    tooltip()
  }
}

const vertices = computed<Nodes>(() => topologyStore.vertices)
const edges = computed<Edges>(() => topologyStore.edges)
const layout = computed<Layouts>(() => topologyStore.getLayout)
const namespace = computed(() => topologyStore.namespace)
const focusObjects = computed<IdLabelProps[]>(() => topologyStore.focusObjects || [])
const highlightFocusedObjects = computed(() => topologyStore.highlightFocusedObjects)
const selectedView = computed(() => topologyStore.selectedView)

const tooltipPos = computed(() => {
  const defaultPos = { left: '-9999px', top: '-99999px' }

  if (!graph.value || !tooltip.value || !targetNodeId.value || !showTooltip.value) return defaultPos

  // attempt to get the node position from the layout. If layout is d3, use the function
  const nodePos = layout.value.nodes ? layout.value.nodes[targetNodeId.value] : getD3NodeCoords()
  if (!nodePos) return defaultPos

  // translate coordinates: SVG -> DOM
  const domPoint = graph.value.translateFromSvgToDomCoordinates(nodePos)

  let additionalOffset = {
    nodeIconWidth: 32,
    tooltipMinWidth: 100,
    tooltipOffsetWidth: tooltip.value.offsetWidth,
    tooltipMinHeight: 30,
    tooltipOffsetHeight: tooltip.value.offsetHeight,
    domPointXAjustment: 0
  }
  let pos = defaultPos

  switch(selectedView.value) {
    case ViewType.circle:
      additionalOffset = {
        ...additionalOffset,
        domPointXAjustment: 4 // adjustment needed to horizontally centered tooltip relatively to node icon
      }

      pos = {
        left: `${
          (Number(domPoint.x)
              + (additionalOffset.nodeIconWidth / 2)
              - ((additionalOffset.tooltipOffsetWidth - additionalOffset.tooltipMinWidth) / 2)
              - additionalOffset.domPointXAjustment)
            .toFixed(0)
        }px`,
        top: `${
          (Number(domPoint.y)
              - (additionalOffset.tooltipOffsetHeight
              - additionalOffset.tooltipMinHeight))
            .toFixed(0)
        }px`
      }

      break
    case ViewType.d3:
      pos = {
        left: `${Number(domPoint.x).toFixed(0)}px`,
        top: `${Number(domPoint.y).toFixed(0)}px`
      }

      break
    default:
  }

  return pos
})

const eventHandlers: EventHandlers = {
  // on right clicking background
  'view:contextmenu': ({ event }: ViewEvent<any>) => {
    event.preventDefault()
    contextMenuType.value = ContextMenuType.background
    displayContextMenu(event.x, event.y)
  },
  // on right clicking node
  'node:contextmenu': ({ node, event }: NodeEvent<any>) => {
    event.preventDefault()

    // if right clicking on a selected group of nodes
    if (selectedNodes.value.length > 1 && selectedNodes.value.includes(node)) {
      groupClick.value = true
      getNodesFromSelectedIds()
    } else {
      groupClick.value = false
      selectedNodeObjects.value = []
    }

    contextMenuType.value = ContextMenuType.node
    contextNode.value = vertices.value[node]
    displayContextMenu(event.x, event.y)
  },
  // on hover, display tooltip
  'node:pointerover': ({ node }: NodeEvent<any>) => {
    targetNodeId.value = node
    displayTooltip(true)
  },
  'node:pointerout': () => {
    displayTooltip(false)
  },
  'node:dragstart': () => {
    d3ForceEnabled.value = false // to keep other nodes in place when one is dragged
    displayTooltip(false)
  },
  'node:dragend': (node) => {
    // get node's position for tooltip
    if(selectedView.value === ViewType.d3) {
      const nodeId = Object.keys(node)[0]
      const {x: nodeX, y: nodeY} = Object.values(node)[0]
      d3Nodes.value.forEach(d3Node => {
        if(d3Node.id === nodeId) {
          d3Node.x = nodeX
          d3Node.y = nodeY
        }
      })
    }

    displayTooltip(true)
  }
}

const getNodesFromSelectedIds = () => {
  selectedNodeObjects.value = selectedNodes.value.map((nodeId) => {
    return vertices.value[nodeId]
  })
}

const forceLayout = new ForceLayout({
  positionFixedByDrag: true,
  createSimulation: (d3, nodes, edges) => {
    const forceLink = d3.forceLink<ForceNodeDatum, ForceEdgeDatum>(edges).id(d => d.id)
    const force = d3
      .forceSimulation(nodes)
      .force('edge', forceLink.distance(100))
      .force('charge', d3.forceManyBody().distanceMax(300))
      .force('collide', d3.forceCollide(10))
      .force('center', d3.forceCenter(0, 0))
      .force('x', d3.forceX(0).strength(0.01))
      .force('y', d3.forceY(0).strength(0.01))
      .alphaMin(0.001)

    d3Nodes.value = force.nodes() as D3Node[]

    return force
  }
})

const d3ForceEnabled = computed({
  get: () => configs.view?.layoutHandler instanceof ForceLayout,
  set: (value: boolean) => {
    if (configs.view) {
      configs.view.layoutHandler = value ? forceLayout : new SimpleLayout()
    }
  }
})

const trigger = ref(true)

watch(layout, async (layout) => {
  d3ForceEnabled.value = Object.keys(layout).length === 0

  trigger.value = false
  await nextTick()
  trigger.value = true
})

watch(namespace, async () => {
  // to have d3Nodes with coordinates for tooltip positioning
  d3ForceEnabled.value = topologyStore.selectedView === ViewType.d3

  trigger.value = false
  await nextTick()
  trigger.value = true
})

const setColor = (item: Node | Edge) => {
  if (highlightFocusedObjects.value && !item.focused) {
    return 'rgb(39, 49, 128, 0.5)'
  }

  return 'rgb(39, 49, 128)' // feather primary
}

const configs = reactive(
  defineConfigs({
    view: {
      layoutHandler: topologyStore.selectedView === ViewType.d3 ? forceLayout : new SimpleLayout()
    },
    node: {
      selectable: true,
      normal: {
        type: ViewType.circle,
        color: (node: Node) => setColor(node)
      }
    },
    edge: {
      normal: {
        color: (edge: Edge) => setColor(edge)
      }
    }
  })
)
</script>

<style
  lang="scss"
  scoped
>
@use "@featherds/styles/themes/variables";
@use "@featherds/styles/mixins/elevation";
@use "@featherds/styles/mixins/typography";

// transitions when scaling on mouseover.
.node-circle,
.node-icon {
  transition: all 0.1s linear;
}

.unfocused {
  opacity: 0.5;
  background: var(variables.$state-color-on-surface);
}

.tooltip-wrapper {
  position: relative;
  display: contents;

  .tooltip {
    @include elevation.elevation(2);
    @include typography.subtitle1;
    top: 0;
    left: 0;
    display: "none";
    position: absolute;
    width: auto;
    min-width: 100px;
    height: auto;
    min-height: 30px;
    padding: 10px;
    text-align: center;
    font-size: 12px;
    background-color: var(variables.$surface);
    border: 1px solid var(variables.$primary);
  }
}
</style>
