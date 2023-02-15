<template>
  <section class="help-configuring">
    <div class="subtitle1">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.heading }}</div>
    <ul class="type-square">
      <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.list.text1 }}</li>
      <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.list.text2 }}</li>
      <li
        @click="isHelpOpen = !isHelpOpen"
        class="step-opener"
      >
        <span>
          {{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.top.list.text3 }}
          <FeatherIcon :icon="isHelpOpen ? expandLessIcon : expandMoreIcon" />
        </span>
      </li>
    </ul>
    <div
      v-if="isHelpOpen"
      class="steps"
    >
      <div class="stepper">
        <div
          @click="stepHandler(0)"
          class="arrow"
          :class="step === 1 ? 'disabled' : ''"
        >
          <FeatherIcon :icon="chevronLeftIcon" />
        </div>
        <div class="step-line">
          <div class="line"></div>
          <ul>
            <li
              @click="stepHandler(1)"
              :class="step === 1 ? 'selected' : ''"
            >
              1
            </li>
            <li
              @click="stepHandler(2)"
              :class="step === 2 ? 'selected' : ''"
            >
              2
            </li>
            <li
              @click="stepHandler(3)"
              :class="step === 3 ? 'selected' : ''"
            >
              3
            </li>
            <li
              @click="stepHandler(4)"
              :class="step === 4 ? 'selected' : ''"
            >
              4
            </li>
          </ul>
        </div>
        <div
          @click="stepHandler(5)"
          class="arrow"
          :class="step === 4 ? 'disabled' : ''"
        >
          <FeatherIcon :icon="chevronRightIcon" />
        </div>
      </div>
      <div
        v-if="step === 1"
        class="step-content"
      >
        <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.heading }}</div>
        <ul class="type-square">
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.list.text1 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.list.text2 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step1.list.text3 }}</li>
        </ul>
      </div>
      <div
        v-if="step === 2"
        class="step-content"
      >
        <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.heading }}</div>
        <ul class="type-square">
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.list.text1 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.list.text2 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step2.list.text3 }}</li>
        </ul>
      </div>
      <div
        v-if="step === 3"
        class="step-content"
      >
        <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.heading }}</div>
        <ul class="type-square">
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.list.text1 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.list.text2 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step3.list.text3 }}</li>
        </ul>
      </div>
      <div
        v-if="step === 4"
        class="step-content"
      >
        <div class="subtitle2">{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.heading }}</div>
        <ul class="type-square">
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.list.text1 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.list.text2 }}</li>
          <li>{{ discoveryText.DiscoverySyslogSNMPTrapsForm.help.step4.list.text3 }}</li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import ExpandMoreIcon from '@featherds/icon/navigation/ExpandMore'
import ExpandLessIcon from '@featherds/icon/navigation/ExpandLess'
import ChevronLeftIcon from '@featherds/icon/navigation/ChevronLeft'
import ChevronRightIcon from '@featherds/icon/navigation/ChevronRight'
import discoveryText from './discovery.text'

const step = ref(1)
const isHelpOpen = ref(false)
const stepHandler = (s: number) => {
  switch (s) {
    case 0: // previous
      if (step.value !== 1) step.value = step.value - 1
      break
    case 5: // next
      if (step.value !== 4) step.value = step.value + 1
      break
    default:
      step.value = s
  }
}

const expandMoreIcon = markRaw(ExpandMoreIcon)
const expandLessIcon = markRaw(ExpandLessIcon)
const chevronLeftIcon = markRaw(ChevronLeftIcon)
const chevronRightIcon = markRaw(ChevronRightIcon)
</script>

<style lang="scss" scoped>
@use '@featherds/styles/mixins/typography';
@use '@featherds/styles/themes/variables';
@use '@/styles/mediaQueriesMixins.scss';

.help-configuring {
  border-left: 1px solid var(variables.$border-on-surface);
  padding-left: 1rem;
  .subtitle1 {
    @include typography.subtitle1();
    margin-bottom: 1rem;
  }
  .subtitle2 {
    @include typography.subtitle2();
    margin-bottom: 1rem;
  }
  ul.type-square > li {
    list-style-type: square;
  }
  > ul.type-square {
    margin-left: 1.5rem;
    margin-bottom: 1rem;
  }
  .step-opener {
    > span {
      color: var(variables.$secondary-variant);
      &:hover {
        cursor: pointer;
      }
      .feather-icon {
        width: 1.2rem;
        height: 1.2rem;
        vertical-align: bottom;
      }
    }
  }
  .steps {
    margin-left: 2rem;
    .stepper {
      display: flex;
      margin-bottom: 1rem;
      .arrow {
        width: 10%;
        text-align: center;
        &:hover {
          cursor: pointer;
        }
      }
      .step-line {
        width: 80%;
        position: relative;
        .line {
          border: 1px solid var(variables.$border-on-surface);
          background-color: var(variables.$background);
          width: 100%;
          position: absolute;
          top: 1rem;
          left: 0;
          z-index: 1000;
        }
        > ul {
          display: flex;
          justify-content: space-between;
          position: relative;
          z-index: 1001;
          > li {
            border: 1px solid var(variables.$border-on-surface);
            border-radius: 50%;
            width: 2rem;
            height: 2rem;
            text-align: center;
            padding-top: 2px;
            color: var(variables.$secondary-text-on-surface);
            background-color: var(variables.$surface);
            &:hover {
              cursor: pointer;
            }
          }
        }
        .selected {
          background-color: var(variables.$background);
        }
      }
      .disabled {
        color: var(variables.$disabled-text-on-surface);
        &:hover {
          cursor: default;
        }
      }
      .feather-icon {
        width: 1.2rem;
        height: 1.2rem;
        vertical-align: bottom;
      }
    }
    .step-content {
      margin-right: 2rem;
      margin-left: 2rem;
      > ul {
        margin-left: 1rem;
      }
    }
  }
}
</style>
