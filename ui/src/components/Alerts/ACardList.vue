<template>
  <div class="card-list">
    <div class="card-list-top">
      <div class="select-all">
        <FeatherCheckbox v-model="selectAll" />
        <FeatherButton
          @click="acknowledgeAll"
          secondary
          >acknowledged selected</FeatherButton
        >
        <div class="list-count">1-50 of 100</div>
      </div>
    </div>
    <div class="content">
      <div
        v-if="!mock.length"
        class="empty-list"
      >
        No results found. Refine or reduce filter criteria.
        <FeatherButton secondary>clear all filters</FeatherButton>
      </div>
      <div v-else>
        <div
          v-for="alert in mock"
          :key="alert.id"
          class="card"
        >
          <FeatherCheckbox
            :model-value="isAlertSelected"
            @update:model-value="alertSelectedHandler(alert.id)"
          />
          <ACard :alert="alert" />
        </div>
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
    name: 'alert1',
    severity: 'Critical',
    cause: 'Power supply failure',
    duration: '3hrs', //
    node: 'Server',
    date: '99-99-9999',
    time: '99:99:99',
    acknowledged: true,
    description:
      'Sit lorem kasd diam clita tempor ipsum justo invidunt. Elitr ut labore clita ea nonumy vero sanctus lorem. Dolores ipsum justo kasd consetetur. Dolore diam sit dolor amet clita nonumy lorem sanctus, diam voluptua kasd labore eos sadipscing, sit erat est invidunt sit stet erat et sit dolore, sit labore sadipscing nonumy diam aliquyam sanctus ea, ipsum et labore sanctus et duo sed labore amet, dolor tempor stet at sea. Stet dolores et est aliquyam. Et accusam kasd amet justo ut gubergren. Magna rebum duo sadipscing lorem kasd. Eirmod tempor dolor lorem amet at takimata consetetur voluptua. Diam labore at sanctus accusam. Accusam at lorem et amet rebum dolor at ipsum, sanctus clita lorem eirmod sit magna accusam, clita dolor no ut ea dolores no eirmod ut stet, diam sadipscing no takimata amet ipsum amet. Accusam dolor dolores takimata ut dolor sed rebum. Lorem eos consetetur ea nonumy diam nonumy gubergren invidunt consetetur. Duo labore voluptua takimata sit ipsum eirmod, consetetur vero sed at eirmod ea dolor est nonumy ipsum. Duo et accusam lorem sit dolor est dolor dolor, eirmod sea aliquyam clita et erat eos kasd est amet. Sed consetetur accusam elitr diam duo clita ut diam. Lorem magna.'
  }
]

const selectAll = ref(false)
watch(selectAll, (isSelected) => {
  console.log('isSelected', isSelected)
  isAlertSelected.value = isSelected
})

const isAlertSelected = ref(false)
const alertSelectedHandler = (id: number) => {
  console.log('id', id)
  isAlertSelected.value = !isAlertSelected.value
}

const acknowledgeAll = () => {
  // send request
}
</script>

<style lang="scss" scoped></style>
