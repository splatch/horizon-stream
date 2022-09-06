<template>
<h1>{{props.label}} (id: {{route.params.id}})</h1>
</template>

<script setup lang="ts">
const props = defineProps<{
  label: string
}>()
const route = useRoute()


</script>

<style lang="scss">

</style>