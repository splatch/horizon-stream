<template>
<div id="widget-bar">
  <div id="widget-flex-container">
    <FeatherCheckboxGroup label="Select some widgets.">

      <FeatherCheckbox 
        v-if="route.path !== '/'" 
        v-model="minions" 
        @update:modelValue="triggerMinions">
          Minions Widget
      </FeatherCheckbox>

      <FeatherCheckbox 
        v-if="route.path !== '/'" 
        v-model="devices" 
        @update:modelValue="triggerDevices">
          Devices Widget
      </FeatherCheckbox>

      <FeatherCheckbox 
        v-if="!route.path.includes('/map')" 
        v-model="map" 
        @update:modelValue="triggerMap">
          Map Widget
      </FeatherCheckbox>

    </FeatherCheckboxGroup>
  </div>
</div>
</template>

<script setup lang="ts">
import { createVNode, render } from 'vue'

const route = useRoute()

const minions = ref(false)
const devices = ref(false)
const map = ref(false)

const triggerMinions = (val: boolean | undefined) => {
  if (val) addWidget(widgets.minionsTable)
  else removeWidget(widgets.minionsTable)
}

const triggerDevices = (val: boolean | undefined) => {
  if (val) addWidget(widgets.devicesTable)
  else removeWidget(widgets.devicesTable)
}

const triggerMap = (val: boolean | undefined) => {
  if (val) addWidget(widgets.map, { height: '400px' })
  else removeWidget(widgets.map)
}

const instance = getCurrentInstance()

const widgets = {
  minionsTable: '../Appliances/MinionsTable.vue',
  devicesTable: '../Appliances/DeviceTable.vue',
  map: '../Map/LeafletMap.vue'
}

const addWidget = (widget: string, styles?: { height: string }) => {
  const widgetContainer = document.getElementById('widget-flex-container') as HTMLDivElement
  const component = defineAsyncComponent(() => import(widget))
  const div = document.createElement('div')
  div.id = widget

  if (styles) {
    div.style.height = styles.height
  }

  const vNode = createVNode(component)
  vNode.appContext = instance?.appContext as any
  render(vNode, div)
  widgetContainer.appendChild(div)
}

const removeWidget = (widget: string) => {
  const div = document.getElementById(widget) as HTMLDivElement
  render(null, div)
  div.remove()
}
</script>

<style scoped lang="scss">
#widget-bar {
  display: block;
  width: 500px;
  height: calc(100vh - 62px);

  #widget-flex-container {
    display: flex;
    flex-direction: column;
    margin-top: 10px;
    padding: 10px;
    gap: 10px;
  }
}
</style>