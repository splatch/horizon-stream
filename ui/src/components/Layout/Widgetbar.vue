<template>
<div id="widget-bar">
  <div id="widget-flex-container">
    <FeatherCheckboxGroup label="Available Widgets">
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
import { ComponentInternalInstance } from 'vue';
import { addWidget, removeWidget} from '../Widgets/utils'

const route = useRoute()
const minions = ref(false)
const devices = ref(false)
const map = ref(false)
const widgetContainer = () => document.getElementById('widget-flex-container') as HTMLDivElement
const instance = getCurrentInstance() as ComponentInternalInstance

const widgets = {
  deviceWidget: '../Widgets/DeviceWidget.vue',
  minionWidget: '../Widgets/MinionWidget.vue',
  mapWidget: '../Widgets/MapWidget.vue'
}

const triggerMinions = (val: boolean | undefined) => {
  if (val) addWidget(instance, widgets.minionWidget, widgetContainer())
  else removeWidget(widgets.minionWidget)
}

const triggerDevices = (val: boolean | undefined) => {
  if (val) addWidget(instance, widgets.deviceWidget, widgetContainer())
  else removeWidget(widgets.deviceWidget)
}

const triggerMap = (val: boolean | undefined) => {
  if (val) addWidget(instance, widgets.mapWidget, widgetContainer(), {}, { height: '400px' })
  else removeWidget(widgets.mapWidget)
}
</script>

<style scoped lang="scss">
@import "@featherds/styles/themes/variables";

#widget-bar {
  display: block;
  width: 500px;
  height: calc(100vh - 62px);
  border-left: 3px solid var($primary);

  #widget-flex-container {
    display: flex;
    flex-direction: column;
    margin-top: 10px;
    padding: 10px;
    gap: 10px;
  }
}
</style>