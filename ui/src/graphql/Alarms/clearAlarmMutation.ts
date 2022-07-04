export default `mutation clearAlarm ($id: Long!, $ackDTO: AlarmAckDTOInput!) {
  clearAlarm (id: $id, ackDTO: $ackDTO)
}`