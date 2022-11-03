<template>
  <FeatherIcon
    :icon="Settings"
    class="pointer ctrl-icon"
    @click="openModal()"
  />

  <PrimaryModal title="Help improve your experience" :visible="isVisible">
    <template v-slot:content>
      <p>
        Please opt-in to send anonymous usage statistics. This will help <br />
        us improve your software. You can change this setting at any time.
      </p>
    </template>

    <template v-slot:footer>
      <FeatherButton 
        data-test="opt-out-btn" 
        secondary 
        @click="opt()">
          Opt-out
      </FeatherButton>
      
      <FeatherButton 
        data-test="opt-in-btn" 
        primary
        @click="opt(true)">
          Opt-in
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script setup lang="ts">
import Settings from "@featherds/icon/action/Settings"
import useModal from '@/composables/useModal'
import { useUsageStatsMutations } from '@/store/Mutations/usageStatsMutations'

const { openModal, closeModal, isVisible } = useModal()
const store = useUsageStatsMutations()

const opt = (choice: boolean = false) => {
  store.toggleUsageStats({ 
    toggleDataChoices: {
      toggle: choice
    }
  })

  closeModal()
}
</script>

<style scoped lang="scss">
.ctrl-icon {
  font-size: 24px;
  margin-top: 2px;
  margin-right: 15px;
}
</style>
