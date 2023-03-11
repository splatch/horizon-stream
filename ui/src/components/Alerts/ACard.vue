<template>
  <div class="card">
    <div class="name">{{ alert.name }}</div>
    <div class="severity">{{ alert.severity }}</div>
    <div class="cause">{{ alert.cause }}</div>
    <!-- duration: hrs, days, weeks. months? -->
    <div class="duration">{{ alert.duration }}</div>
    <div class="node">{{ alert.node }}</div>
    <div class="date">{{ alert.date }}</div>
    <div class="time">{{ alert.time }}</div>
    <div class="acknowledged">
      <FeatherCheckbox
        :model-value="acknowledgedChecked"
        @update:model-value="acknowledgeHandler(alert.id)"
        >Acknowledged</FeatherCheckbox
      >
    </div>
    <div class="expander-ctrl">
      <FeatherButton icon="Expand">
        <FeatherIcon :icon="icon.ExpandMore" />
      </FeatherButton>
    </div>
    <div class="desciption">{{ alert.description }}</div>
  </div>
</template>

<script lang="ts" setup>
import ExpandMore from '@featherds/icon/navigation/ExpandMore'
import ExpandLess from '@featherds/icon/navigation/ExpandLess'

const props = defineProps({
  alert: {
    type: Object,
    required: true
  }
})

const acknowledgedChecked = ref(props.alert.acknowledged)
const acknowledgeHandler = (id: number) => {
  console.log('id', id)
  acknowledgedChecked.value = !acknowledgedChecked.value
  // send request
}

const icon = markRaw({
  ExpandMore,
  ExpandLess
})
</script>

<style lang="scss" scoped></style>
