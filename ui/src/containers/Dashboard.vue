<template>
  <p class="title">Horizon Stream</p>
  <p class="welcome" v-if="user">Welcome, {{ user }}</p>
  <AlarmsTable />
</template>

<script setup lang="ts">
import { useAuthStore } from '@/store/authStore'
const authStore = useAuthStore()
const user = computed(() => authStore.userInfo?.preferred_username)
</script>

<style scoped lang="scss">
@import "@featherds/styles/mixins/typography";

p {
  text-align: center;
}
.title {
  @include headline2;
  margin-top: 10px;
}
.welcome {
  @include headline4;
}
</style>
