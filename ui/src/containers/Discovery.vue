<template>
  <PageHeader
    :heading="discoveryText.Discovery.heading"
    class="header"
  />
  <div class="container">
    <section class="my-discovery">
      <div class="add-btn">
        <FeatherButton
          @click="showDiscoveryEditing"
          primary
        >
          {{ discoveryText.Discovery.button.add }}
          <template #icon>
            <Icon :icon="addIcon" />
          </template>
        </FeatherButton>
      </div>
      <div class="my-discovery-inner">
        <!-- dropdown select discovery (edit) -->
        <div>Search/Filter discovery</div>
        <div>
          <div>My active discovery</div>
          <div>You have no active discovery</div>
        </div>
        <div>
          <div>My passive discovery</div>
          <div>You have no active discovery</div>
        </div>
      </div>
    </section>

    <!-- add/edit a discovery  -->
    <section
      v-if="isDiscoveryEditingShown"
      class="discovery"
    >
      <h5>{{ discoveryText.Discovery.heading1 }}</h5>
      <div>
        <!-- active -->
        <!-- passive -->
      </div>
      <DiscoverySyslogSNMPTrapsForm
        v-if="discoverySelectedType === DiscoveryType.SyslogSNMPTraps"
        @cancel-editing="discoverySelectedType = DiscoveryType.None"
      />
      <div
        v-if="discoverySelectedType === DiscoveryType.None"
        class="get-started"
      >
        {{ discoveryText.Discovery.noneDiscoverySelectedMsg }}
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import AddIcon from '@featherds/icon/action/Add'
import { IIcon } from '@/types'
import { DiscoveryType } from '@/components/Discovery/discovery.constants'
import discoveryText from '@/components/Discovery/discovery.text'
import DiscoverySyslogSNMPTrapsForm from '@/components/Discovery/DiscoverySyslogSNMPTrapsForm.vue'

const isDiscoveryEditingShown = ref(true)
const discoverySelectedType = ref(DiscoveryType.SyslogSNMPTraps)

const showDiscoveryEditing = () => {
  isDiscoveryEditingShown.value = true
}

const addIcon: IIcon = {
  image: markRaw(AddIcon)
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';
@use '@/styles/vars.scss';

.container {
  display: flex;
  flex-direction: row;
  flex-flow: wrap;
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);

  @include mediaQueriesMixins.screen-md {
    column-gap: 3%;
    > .my-discovery {
      width: 30%;
      min-width: auto;
    }
    > .discovery {
      width: 67%;
      min-width: auto;
    }
  }
}

.my-discovery {
  width: 100%;
  min-width: 400px;

  .add-btn {
    width: 100%;
    margin-bottom: var(variables.$spacing-l);
    border-bottom: 1px solid var(variables.$border-on-surface);
    > button {
      margin-bottom: var(variables.$spacing-l);
    }
  }

  > .my-discovery-inner {
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-bottom: var(variables.$spacing-l);
    > * {
      border: 1px solid var(variables.$border-on-surface);
      border-radius: vars.$border-radius-s;
      padding: var(variables.$spacing-m);
      margin-bottom: var(variables.$spacing-m);
      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  @include mediaQueriesMixins.screen-sm {
    flex-direction: row;
    column-gap: 2%;
    > * {
      width: 32%;
      margin-bottom: 0;
    }
  }
  @include mediaQueriesMixins.screen-md {
    flex-direction: column;
    margin-bottom: 0;
    > * {
      width: 100%;
      margin-bottom: var(variables.$spacing-m);
    }
  }
}

.discovery {
  width: 100%;
  min-width: 400px;
  border: 1px solid var(variables.$border-on-surface);
  border-radius: vars.$border-radius-s;
  padding: var(variables.$spacing-m);
  h4,
  h5 {
    margin-bottom: var(variables.$spacing-m);
  }
}

.header {
  margin-left: var(variables.$spacing-l);
  margin-right: var(variables.$spacing-l);
}
</style>
