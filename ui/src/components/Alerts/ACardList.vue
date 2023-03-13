<template>
  <div class="card-list">
    <div class="card-list-top">
      <div class="select-all-checkbox-btn">
        <FeatherCheckbox
          v-model="isAllAlertsSelected"
          data-test="select-all-checkbox"
        />
        <FeatherButton
          @click="acknowledgeSelectedAlerts"
          :disabled="!atLeastOneAlertSelected"
          secondary
          data-test="acknowledge-all-btn"
          >acknowledged selected</FeatherButton
        >
      </div>
      <div class="list-count">1-50 of 100</div>
    </div>
    <div class="content">
      <div v-if="mock.length">
        <ACard
          v-for="alert in alerts"
          :key="alert.id"
          :alert="alert"
          @alert-selected="alertSelectedListener"
        />
      </div>
      <div
        v-else
        class="empty-list"
      >
        No results found. Refine or reduce filter criteria.
        <FeatherButton secondary>clear all filters</FeatherButton>
      </div>
    </div>
    <div class="card-list-bottom">
      <!-- card pagination -->
      <div class="list-count">1-50 of 100</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
const mock = [
  {
    id: 1,
    name: 'alert1 alert1 alert1 alert1 alert1 alert1',
    severity: 'Critical',
    cause: 'Power supply failure',
    duration: '3hrs',
    node: 'Server',
    date: '99-99-9999',
    time: '00:00:00',
    acknowledged: true,
    description:
      'Sit lorem kasd diam clita tempor ipsum justo invidunt. Elitr ut labore clita ea nonumy vero sanctus lorem. Dolores ipsum justo kasd consetetur. Dolore diam sit dolor amet clita nonumy lorem sanctus, diam voluptua kasd labore eos sadipscing, sit erat est invidunt sit stet erat et sit dolore, sit labore sadipscing nonumy diam aliquyam sanctus ea, ipsum et labore sanctus et duo sed labore amet, dolor tempor stet at sea. Stet dolores et est aliquyam. Et accusam kasd amet justo ut gubergren. Magna rebum duo sadipscing lorem kasd. Eirmod tempor dolor lorem amet at takimata consetetur voluptua. Diam labore at sanctus accusam. Accusam at lorem et amet rebum dolor at ipsum, sanctus clita lorem eirmod sit magna accusam, clita dolor no ut ea dolores no eirmod ut stet, diam sadipscing no takimata amet ipsum amet. Accusam dolor dolores takimata ut dolor sed rebum. Lorem eos consetetur ea nonumy diam nonumy gubergren invidunt consetetur. Duo labore voluptua takimata sit ipsum eirmod, consetetur vero sed at eirmod ea dolor est nonumy ipsum. Duo et accusam lorem sit dolor est dolor dolor, eirmod sea aliquyam clita et erat eos kasd est amet. Sed consetetur accusam elitr diam duo clita ut diam. Lorem magna.'
  },
  {
    id: 2,
    name: 'alert2 alert2 alert2 alert2 alert2 alert2',
    severity: 'Critical',
    cause: 'Power supply failure',
    duration: '3hrs',
    node: 'Server',
    date: '99-99-9999',
    time: '00:00:00',
    acknowledged: false,
    description:
      'Sit lorem kasd diam clita tempor ipsum justo invidunt. Elitr ut labore clita ea nonumy vero sanctus lorem. Dolores ipsum justo kasd consetetur. Dolore diam sit dolor amet clita nonumy lorem sanctus, diam voluptua kasd labore eos sadipscing, sit erat est invidunt sit stet erat et sit dolore, sit labore sadipscing nonumy diam aliquyam sanctus ea, ipsum et labore sanctus et duo sed labore amet, dolor tempor stet at sea. Stet dolores et est aliquyam. Et accusam kasd amet justo ut gubergren. Magna rebum duo sadipscing lorem kasd. Eirmod tempor dolor lorem amet at takimata consetetur voluptua. Diam labore at sanctus accusam. Accusam at lorem et amet rebum dolor at ipsum, sanctus clita lorem eirmod sit magna accusam, clita dolor no ut ea dolores no eirmod ut stet, diam sadipscing no takimata amet ipsum amet. Accusam dolor dolores takimata ut dolor sed rebum. Lorem eos consetetur ea nonumy diam nonumy gubergren invidunt consetetur. Duo labore voluptua takimata sit ipsum eirmod, consetetur vero sed at eirmod ea dolor est nonumy ipsum. Duo et accusam lorem sit dolor est dolor dolor, eirmod sea aliquyam clita et erat eos kasd est amet. Sed consetetur accusam elitr diam duo clita ut diam. Lorem magna.'
  }
]

const alerts = ref(mock.map((a) => ({ ...a, isSelected: false })))
const atLeastOneAlertSelected = computed(() => alerts.value.some(({ isSelected }) => isSelected))

const isAllAlertsSelected = ref(false)
watch(isAllAlertsSelected, (isSelected) => {
  alerts.value = alerts.value.map((a) => ({
    ...a,
    isSelected: isSelected
  }))
})

const alertSelectedListener = (id: number) => {
  alerts.value = alerts.value.map((a) => {
    if (a.id === id) {
      a.isSelected = !a.isSelected // selection toggle
    }

    return a
  })
}

const acknowledgeSelectedAlerts = () => {
  // send request
}
</script>

<style lang="scss" scoped>
@use '@featherds/styles/themes/variables';

.card-list-top {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(variables.$spacing-m) var(variables.$spacing-l);
}

.content {
  margin-bottom: var(variables.$spacing-l);
}

.select-all-checkbox-btn {
  display: flex;
  align-items: center;
}

.select-all-checkbox-btn {
  :deep(.layout-container) {
    margin-bottom: 0;
    .feather-checkbox {
      padding-right: var(variables.$spacing-xs);
      label {
        display: none;
      }
    }
  }
}

.card-list-bottom {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(variables.$spacing-s);
}
</style>
