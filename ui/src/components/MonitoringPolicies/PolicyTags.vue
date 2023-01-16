<template>
  <div class="tags">
    <div class="select-tags">
      <div class="title">Select Tags:</div>
      <div class="list-tags">
        <div
          v-for="tag in tags"
          :key="tag"
        >
          <FeatherCheckbox>
            {{tag}}
          </FeatherCheckbox>
        </div>
      </div>
    </div>
    <div class="create-tags">
      <div class="title">Create Tag:</div>
      <FeatherInput
        v-model="newTag"
        label="New tag"
        class="new-tag-input"
      />
      <FeatherButton
        primary
        class="new-tag-btn"
        @click="addTag"
      >
        Add Tag
      </FeatherButton>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
	tags: string[],
}>()
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use "@featherds/styles/mixins/typography";


.tags {
    margin-top: var(variables.$spacing-m);
    background-color: #c3c3c31f;
  	padding: var(variables.$spacing-l);
    border: 1px var(variables.$shade-4) solid;
    display: flex;
    width:100%;
    .title {
        @include typography.subtitle1;
      }
    .select-tags {
      width: 70%;

      > .list-tags {
        display: flex;
        height: 200px;
        flex-direction: column;
        flex-wrap: wrap;
      }
    }

    .create-tags {
      width: 30%;
      border-left: 1px var(variables.$shade-4) solid;
      padding-left:  var(variables.$spacing-l);

      .new-tag-input {
        width: 100%;
        margin: var(variables.$spacing-l) 0;
      }
    }
    .layout-container {
    margin-bottom: 0!important;
    }

}
</style>
