<template>
  <div class="new-policy">
    <div class="title">New Policy</div>
    <div class="form-new-policy">
      <FeatherInput
        v-model="store.name"
        label="New Policy Name"
        class="name-policy"
      />
      <FeatherButton
        class="btn-add-tag"
        secondary
        @click="showTags = !showTags"
      >
        Add Tags <FeatherIcon :icon="showTags ? ExpandLess : ExpandMore" />
      </FeatherButton>
    </div>
  </div>
  <div class="tags-section">
    <PolicyTags
      v-if="showTags"
      :tags="tags"
    />
  </div>
</template>

<script lang="ts" setup>
import { useMonitoringPoliciesStore } from '@/store/Views/monitoringPoliciesStore'
const store = useMonitoringPoliciesStore()
import ExpandMore from '@featherds/icon/navigation/ExpandMore'
import ExpandLess from '@featherds/icon/navigation/ExpandLess'
const showTags = ref(false)

const tags = ref(['tag1', 'tag2', 'tag3', 'tag4', 'tag5', 'tag6', 'tag7', 'tag8', 'tag9', 'tag6=10', 'tag11', 'tag12', 'tag13', 'tag14', 'tag15', 'tag16', 'tag17', 'tag18', 'tag19'])
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';
@use "@featherds/styles/mixins/typography";

.new-policy {
    background-color: var(variables.$surface);
  	padding: var(variables.$spacing-l);
  	margin-top: var(variables.$spacing-m);
    border: 1px var(variables.$shade-4) solid;
    width: 80%;

    .title {
       @include typography.headline4;
       margin-bottom: var(variables.$spacing-s);
    }

    .name-policy {
        width: 350px;
        margin-right: var(variables.$spacing-l);
    }
    .form-new-policy {
        display: flex;
        .btn-add-tag {
            height: 40px;
        }
    }
}

.tags-section {
    width: 80%;
}

:deep(.feather-input-sub-text) {
    display: none;
}
</style>
