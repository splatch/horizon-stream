<template>
  <div class="btns">
    <NotificationsCtrl />
    <AddDeviceCtrl />
  </div>
  <div class="minions-device-container">
    <div class="minions-table"><MinionsTable /></div>
    <div class="device-table"><DeviceTable /></div>
  </div>
</template>

<script lang="ts" setup>

</script>

<style scoped lang="scss">
.btns {
  display: flex;
  justify-content: flex-end;
  gap: 5px;
}
.minions-device-container {
  display: flex;
}
.minions-table {
  width: 40%;
}
.device-table {
  width: 60%;
}
</style>
