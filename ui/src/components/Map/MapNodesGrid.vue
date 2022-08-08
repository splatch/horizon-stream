<template>
  <div id="wrap">
    <table class="tl1 tl2 tl3" summary="Nodes">
      <thead>
        <tr>
          <FeatherSortHeader
            scope="col"
            property="id"
            :sort="sortStates.id"
            @sort-changed="sortChanged"
          >ID</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="foreignSource"
            :sort="sortStates.foreignSource"
            @sort-changed="sortChanged"
          >FOREIGN SOURCE</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="foreignId"
            :sort="sortStates.foreignId"
            @sort-changed="sortChanged"
          >FOREIGN ID</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="label"
            :sort="sortStates.label"
            @sort-changed="sortChanged"
          >LABEL</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="labelSource"
            :sort="sortStates.labelSource"
            @sort-changed="sortChanged"
          >LABEL SOURCE</FeatherSortHeader>

          <!-- <FeatherSortHeader
            scope="col"
            property="lastCapabilitiesScan"
            :sort="sortStates.lastCapabilitiesScan"
            @sort-changed="sortChanged"
          >LAST CAP SCAN</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="primaryInterface"
            :sort="sortStates.primaryInterface"
            @sort-changed="sortChanged"
          >PRIMARY INTERFACE</FeatherSortHeader> -->

          <FeatherSortHeader
            scope="col"
            property="sysObjectId"
            :sort="sortStates.sysObjectId"
            @sort-changed="sortChanged"
          >SYSOBJECTID</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="sysName"
            :sort="sortStates.sysName"
            @sort-changed="sortChanged"
          >SYSNAME</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="sysDescription"
            :sort="sortStates.sysDescription"
            @sort-changed="sortChanged"
          >SYSDESCRIPTION</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="sysContact"
            :sort="sortStates.sysContact"
            @sort-changed="sortChanged"
          >SYSCONTACT</FeatherSortHeader>

          <FeatherSortHeader
            scope="col"
            property="sysLocation"
            :sort="sortStates.sysLocation"
            @sort-changed="sortChanged"
          >SYSLOCATION</FeatherSortHeader>
        </tr>
      </thead>
      <tbody>
        <tr v-for="device in geomapQueries.devicesForGeomap" :key="(device?.id as number)" @dblclick="doubleClickHandler(device as Partial<DeviceDto>)">
          <td class="first-td" :class="nodeLabelAlarmServerityMap[device?.label as string]">{{ device?.id }}</td>
          <td>{{ device?.foreignSource }}</td>
          <td>{{ device?.foreignId }}</td>
          <td>{{ device?.label }}</td>
          <td>{{ device?.labelSource }}</td>
          <!-- <td v-date>{{ device?.lastCapabilitiesScan }}</td> -->
          <!-- <td>{{ device?.primaryInterface }}</td> -->
          <td>{{ device?.sysOid }}</td>
          <td>{{ device?.sysName }}</td>
          <td>{{ device?.sysDescription }}</td>
          <td>{{ device?.sysContact }}</td>
          <td>{{ device?.sysLocation }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script setup lang="ts">
import { useMapStore } from '@/store/Views/mapStore'
import { useGeomapQueries } from '@/store/Queries/geomapQueries'
import { Coordinates, FeatherSortObject } from '@/types/map'
import { FeatherSortHeader, SORT } from '@featherds/table'
import { DeviceDto } from '@/types/graphql';

const mapStore = useMapStore()
const geomapQueries = useGeomapQueries()
const nodeLabelAlarmServerityMap = computed(() => mapStore.getNodeAlarmSeverityMap)

const doubleClickHandler = (device: Partial<DeviceDto>) => {
  if (device.location?.latitude && device.location.longitude) {
    const coordinate: Coordinates = { latitude: device?.location?.latitude, longitude: device?.location?.longitude }
    mapStore.mapCenter = coordinate
  }
}

const sortStates: any = reactive({
  label: SORT.ASCENDING,
  id: SORT.NONE,
  foreignSource: SORT.NONE,
  foreignId: SORT.NONE,
  labelSource: SORT.NONE,
  lastCapabilitiesScan: SORT.NONE,
  primaryInterface: SORT.NONE,
  sysObjectId: SORT.NONE,
  sysName: SORT.NONE,
  sysDescription: SORT.NONE,
  sysContact: SORT.NONE,
  sysLocation: SORT.NONE
})

const sortChanged = (sortObj: FeatherSortObject) => {
  for (const key in sortStates) {
    sortStates[key] = SORT.NONE
  }
  sortStates[`${sortObj.property}`] = sortObj.value
  mapStore.nodeSortObject = sortObj
}

onMounted(() => {
  const wrap = document.getElementById('wrap')
  const thead = document.querySelector('thead')

  if (wrap && thead) {
    wrap.addEventListener('scroll', function () {
      let translate = 'translate(0,' + this.scrollTop + 'px)'
      thead.style.transform = translate
    })
  }
})
</script>

<style lang="scss" scoped>
@import "@featherds/table/scss/table";
#wrap {
  height: calc(100% - 29px);
  overflow: auto;
  background: var($surface);
}
table {
  @include table;
  @include table-condensed;
  background: var($surface);
  color: var($primary-text-on-surface);
  padding-top: 4px;
  margin-top: 15px;
}
thead {
  z-index: 2;
  position: relative;
  background: var($surface);
  white-space: nowrap;
}
.first-td {
  padding-left: 12px;
  border-left: 4px solid var($success);
}
.WARNING {
  border-left: 4px solid #fffb00ea
}
.MINOR {
  border-left: 4px solid var($warning);
}
.MAJOR {
  border-left: 4px solid #ff3c00;
}

.CRITICAL {
  border-left: 4px solid var($error);
}
</style>
