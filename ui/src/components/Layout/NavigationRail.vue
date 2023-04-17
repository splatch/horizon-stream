<template>
  <FeatherAppRail
    :labels="labels"
    :content="content"
  >
    <template v-slot:icon>
      <IconTextAnimate class="nav-rail-home">
        <template v-slot:icon>
          <FeatherIcon
            :icon="LogoIcon"
            title="Home"
            class="product-icon"
            @click="gotoHome"
          />
        </template>
        <template v-slot:text>
          <FeatherIcon
            :icon="LogoText"
            title="Home"
            class="product-icon"
            @click="gotoHome"
          />
        </template>
      </IconTextAnimate>
    </template>
    <template v-slot:nav>
      <router-link
        to="/"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Home"
          :class="{ selected: isActive }">
          Home
        </FeatherAppRailNavItem>
      </router-link>

      <router-link
        to="/appliances"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Appliances"
          :class="{ selected: isActive }">
          Appliances
        </FeatherAppRailNavItem>
      </router-link>

      <router-link
        to="/map"
        v-slot="{ isActive }">
      <FeatherAppRailNavItem
        :icon="Icons.Location"
        :class="{ selected: isActive }">
        Map
      </FeatherAppRailNavItem>
      </router-link>

      <router-link
        to="/inventory"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Business"
          :class="{ selected: isActive }">
          Inventory
        </FeatherAppRailNavItem>
      </router-link>

      <router-link
        to="/discovery"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Discovery"
          :class="{ selected: isActive }">
          Discovery
        </FeatherAppRailNavItem>
      </router-link>

      <router-link
        to="/monitoring-policies"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Monitoring"
          :class="{ selected: isActive }">
          Monitoring Policies
        </FeatherAppRailNavItem>
      </router-link>

      <router-link
        to="/alerts"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Warning"
          :class="{ selected: isActive }">
          Alerts
        </FeatherAppRailNavItem>
      </router-link>

      <router-link
        v-if="false"
        to="/synthetic-transactions"
        v-slot="{ isActive }">
        <FeatherAppRailNavItem
          :icon="Icons.Cycle"
          :class="{ selected: isActive }">
          Synthetic Transactions
        </FeatherAppRailNavItem>
      </router-link>
    </template>
  </FeatherAppRail>
</template>

<script setup lang="ts">
import { IconTextAnimate, FeatherAppRailNavItem } from '@featherds/app-rail'
import Appliances from '@featherds/icon/hardware/Appliances'
import Home from '@featherds/icon/action/Home'
import Location from '@featherds/icon/action/Location'
import Business from '@featherds/icon/action/Business'
import LogoIcon from '@/assets/OpenNMS-logo-icon.svg'
import LogoText from '@/assets/OpenNMS-logo-text.svg'
import Discovery from '@featherds/icon/action/Search'
import Monitoring from '@featherds/icon/hardware/MinionProfiles'
import Cycle from '@featherds/icon/action/Cycle'
import Warning from '@featherds/icon/notification/Warning'

const Icons = markRaw({
  Appliances,
  Home,
  Location,
  Business,
  Discovery,
  Monitoring,
  Cycle,
  Warning
})

const labels = {
  skip: 'Skip to main content'
}
const content = 'mainContent'

const router = useRouter()
const route = useRoute()

const isSelected = (path: string) => path === route.fullPath

const gotoHome = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/mixins/typography';

.icon-text-animate {
  &:hover {
    cursor: pointer;
  }
}
.product-text {
  @include typography.headline2;
  color: var(--feather-app-rail-text-color);
}
</style>
