<template>
  <div class="container">
    <p>Alarms</p>
    <div class="feather-row">
      <div class="feather-col-12">
        <FeatherButton primary @click="trigger">Send Alarm</FeatherButton>
        <FeatherButton secondary @click="clear">Clear Alarm</FeatherButton>
      </div>
    </div>
    <div class="feather-row">
      <div class="feather-col-12">
        <table summary="Alarms">
          <thead>
            <tr>
              <th>Severity</th>
              <th>Description</th>
              <th>Last Event Time</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="alarm in dashboardViewStore.alarms" :key="alarm?.id || ''">
              <td>
                <div class="severity">
                  <div :class="alarm?.severity?.toLowerCase()" class="status-box"></div>
                  {{ alarm?.severity }}
                </div>
              </td>
              <td>{{ alarm?.description }}</td>
              <td v-date>{{ alarm?.lastEventTime }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getMockEvent } from '@/types/mocks'
import { useAlarmsStore } from '@/store/alarmsStore'
import { useEventsStore } from '@/store/eventsStore'
import { useDashboardViewStore } from '@/store/dashboardViewStore'
import { AlarmAckDtoInput } from '@/graphql/operations'

const dashboardViewStore = useDashboardViewStore()
const eventsStore = useEventsStore()
const alarmsStore = useAlarmsStore()

const trigger = async () => {
  await eventsStore.createEvent({ event: getMockEvent() })
  setTimeout(() => {
    dashboardViewStore.fetch()
  }, 350)
}

const clear = async () => {
  const promises = dashboardViewStore.alarms.map((alarm) => 
    alarmsStore.clearAlarm({ id: alarm?.id, ackDTO: { user: 'admin' } as AlarmAckDtoInput }))

  await Promise.all(promises)
  setTimeout(() => {
    dashboardViewStore.fetch()
  }, 350)
}
</script>

<style lang="scss" scoped>
@import "@featherds/table/scss/table";
@import "@featherds/styles/mixins/typography";
@import "@featherds/styles/mixins/elevation";
.container {
  @include elevation(1);
  background: var($surface);
  padding: 10px 30px 30px 30px;
  width: 700px;
  margin: auto;
  margin-top: 10px;

  p {
    @include headline1;
    margin-bottom: 24px;
  }

  table {
    @include table;

    .severity {
      display: flex;
      .status-box {
        display: block;
        width: 12px;
        height: 12px;
        margin: 5px 10px 0px -10px;

        &.cleared {
          background: var($success);
        }
        &.warning {
          background: var($warning);
        }
      }
    }
  }
}
</style>
