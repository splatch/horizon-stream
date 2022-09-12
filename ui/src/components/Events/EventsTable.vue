<template>
  <div>Events table widget</div>
  <!-- <pre>{{props.data}}</pre> -->
</template>

<script lang="ts" setup>
const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  }
})
</script>