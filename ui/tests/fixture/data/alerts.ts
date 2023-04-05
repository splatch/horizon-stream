// @ts-nocheck
import casual from 'casual'
import { rndSeverity, rndDuration, rndNodeType } from '../../helpers/random'

casual.define('alert', function () {
  /* return {
    id: casual.uuid,
    name: `Alert-${casual.word}-${casual.word}-${casual.word}-${casual.word}`,
    severity: rndSeverity(),
    cause: casual.random_element(['Power supply failure', casual.catch_phrase, casual.catch_phrase]),
    duration: rndDuration(),
    nodeType: rndNodeType(),
    date: '9999-99-99', //casual.date((format = 'YYYY-MM-DD')),
    time: '00:00:00', //casual.time((format = 'HH:mm:ss')),
    isAcknowledged: casual.random_element([true, false]),
    description: casual.description

    
  } */

  return {
    // ackTimeMs: 0,
    // ackUser: 'FE',
    acknowledged: casual.coin_flip,
    // clearKey: '',
    // counter: 1,
    // databaseId: 1,
    description: casual.description,
    // firstEventTimeMs: 0,
    // lastEventId: 0,
    lastUpdateTimeMs: casual.unix_time,
    // location: '',
    // logMessage: '',
    // managedObject: null,
    // reductionKey: 'opennms-prime:uei.opennms.org/generic/traps/EnterpriseDefault:1',
    severity: rndSeverity()
    // tenantId: 'opennms-prime',
    // type: 'ALARM_TYPE_UNDEFINED',
    // uei: ''
  }
})

casual.define('alertsList', function () {
  return [
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert,
    casual.alert
  ]
})

const alert = casual.alert
const alertsList = casual.alertsList

export { alert, alertsList }
