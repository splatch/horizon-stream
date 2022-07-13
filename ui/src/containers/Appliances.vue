<template>
<DeviceTable />
<div class="btns">
  <NotificationsCtrl />
</div>
</template>

<script lang="ts" setup>
import DeviceTable from'@/components/Appliances/DeviceTable.vue'

</script>

<style scoped lang="scss">
.btns {
  display: flex;
  justify-content: flex-end;
}
</style>
