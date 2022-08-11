<template>
  <div class="header-container">
    <div class="welcome" 
      v-if="keycloak?.authenticated">
        Welcome, {{ keycloak.tokenParsed?.preferred_username }}
    </div>
    <div class="btns">
      <NotificationsCtrl />
      <AddDeviceCtrl />
    </div>
  </div>

  <div class="minions-device-container">
    <div class="minions-table"><MinionsTable /></div>
    <div class="device-table"><DeviceTable /></div>
  </div>
</template>

<script lang="ts" setup>
import useKeycloak from '@/composables/useKeycloak'
const { keycloak } = useKeycloak()
</script>

<style scoped lang="scss">
@import "@featherds/styles/mixins/typography";
.header-container {
  display: flex;
  justify-content: space-between;
  margin: 30px 0px 25px 0px;
  .welcome {
    @include headline2;
    font-weight: bold;
  }
  .btns {
    display: flex;
    justify-content: flex-end;
    gap: 20px;
  }
}
.minions-device-container {
  display: flex;
  gap: 20px;
  
  .minions-table {
    width: 40%;
  }
  .device-table {
    width: 60%;
  }
}
</style>
