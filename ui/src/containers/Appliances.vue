<template>
  <div class="btns">
    <AddDeviceCtrl />
    <NotificationsCtrl />
  </div>
  <DeviceTable />
  <br/><br/>
  <MinionsTable />
</template>

<script lang="ts" setup>

</script>

<style scoped lang="scss">
.btns {
  display: flex;
  justify-content: flex-end;
  gap: 5px;
}
</style>
