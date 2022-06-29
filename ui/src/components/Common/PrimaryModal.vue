<template>
  <FeatherDialog v-model="visible" relative :labels="labels" @update:modelValue="$emit('close')">
    <div class="content">
      <!-- Main content -->
      <slot name="content" />

      <!-- Footer content -->
      <template v-slot:footer>
        <slot name="footer" />
      </template>
    </div>
  </FeatherDialog>
</template>

<script setup lang="ts">

const props = defineProps({
  visible: {
    required: true,
    type: Boolean
  },
  title: {
    required: true,
    default: String
  }
})

const labels = reactive({
  title: '',
  close: 'Close'
})

watchEffect(() => labels.title = props.title)
</script>

<style scoped lang="scss">
.content {
  min-height: 300px;
  min-width: 550px;
  position: relative;
}
</style>
