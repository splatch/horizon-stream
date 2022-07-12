<template>
  <FeatherDialog v-model="isModalOpen" relative :labels="labels" @update:modelValue="$emit('close')">
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
import useModal from '@/composables/useModal'

const { isVisible } = useModal()

const isModalOpen = ref(false)

const props = defineProps({
  visible: {
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

// modal can be opened by prop or composable call
watchEffect(() => isModalOpen.value = props.visible || isVisible.value)
watchEffect(() => labels.title = props.title)
</script>

<style scoped lang="scss">
.content {
  min-height: 100px;
  min-width: 450px;
  position: relative;
}
</style>
