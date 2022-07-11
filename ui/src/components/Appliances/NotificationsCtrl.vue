<template>
  <FeatherButton 
    primary
    @click="openNotificationsModal"
    data-test="notifications-btn">
      Outbound Notifications
  </FeatherButton>

  <PrimaryModal title="Outbound Notifications" :visible="isModalOpen">
    <template v-slot:content>
      <p class="title" data-test="notifications-modal">
        Pager Duty
      </p>
      <FeatherInput
        data-test="routing-input"
        label="API Routing Key"
        v-model="routingKey"
      />
    </template>

    <template v-slot:footer>
      <FeatherButton 
        data-test="save-btn" 
        primary
        :disabled="!routingKey"
        @click="save">
          Save
      </FeatherButton>

      <FeatherButton 
        data-test="cancel-btn" 
        secondary 
        @click="closeModal">
          Cancel
      </FeatherButton>
    </template>
  </PrimaryModal>
</template>

<script setup lang="ts">
import { useNotificationMutations } from '@/store/Mutations/notificationMutations'

const notificationMutations = useNotificationMutations()

const isModalOpen = ref(false)
const routingKey = ref()

const openNotificationsModal = () => isModalOpen.value = true
const closeModal = () => isModalOpen.value = false
const save = async () => {
  await notificationMutations.sendPagerDutyRoutingKey({ routingKey: routingKey.value })
  closeModal()
}
</script>

<style scoped lang="scss">
@import "@featherds/styles/mixins/typography";

.title {
  @include headline3;
  text-align: center;
  margin-bottom: 15px;
}
</style>