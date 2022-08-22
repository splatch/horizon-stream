<template>
  <FeatherAppBar :labels="{ skip: 'main' }" content="app" v-if="keycloak?.authenticated">
    <template v-slot:left>
      <FeatherAppBarLink 
        class="app-bar" 
        :icon="Logo" 
        title="Home" 
        type="home" 
        url="/" 
      />
    </template>

    <template v-slot:right>
      <FeatherIcon
        :icon="LightDarkMode"
        class="pointer menu-icon"
        @click="toggleDark()"
      />

      <FeatherIcon
        :icon="LogOut"
        class="pointer menu-icon logout"
        @click="logout()"
      />
    </template>
  </FeatherAppBar>
</template>
    
<script setup lang="ts">
import LightDarkMode from '@featherds/icon/action/LightDarkMode'
import LogOut from '@featherds/icon/action/LogOut'
import Logo from '@/assets/Logo.vue'
import useKeycloak from '@/composables/useKeycloak'
import useTheme from '@/composables/useTheme'
import { logout } from '@/services/authService'

const { keycloak } = useKeycloak()
const { toggleDark } = useTheme()
</script>

<style lang="scss">
@import "@featherds/styles/themes/open-mixins";
body {
  background: var($background);

  .app-bar {
    .logo {
      width: 8em !important;
    }
  }
}
.open-light {
  @include open-light;
}
.open-dark {
  @include open-dark;
}
.menu-icon {
  font-size: 24px;
  margin-top: 2px;
  margin-right: 15px;
  &.logout {
    margin-right: 45px;
  }
}
</style>
  