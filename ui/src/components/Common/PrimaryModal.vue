<template>
  <FeatherDialog hideClose :hide-title="hideTitle" v-model="visible" :labels="labels" @update:modelValue="$emit('close')">
      <div class="content">
        <!-- Main content -->
        <slot name="content" />
      </div>

      <!-- Footer content -->
      <template v-slot:footer>
        <slot name="footer" />
      </template>
  </FeatherDialog>
</template>

<script setup lang="ts">
const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  hideTitle: {
    type: Boolean,
    default: false
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
  min-height: 100px;
  min-width: 450px;
  position: relative;
}
</style>
