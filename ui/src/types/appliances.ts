export interface DeviceCreateDTOInput {
  label: string, // required
  location?: string,
  latitude?: number | any,
  longitude?: number | any,
  monitoringArea?: string,
  managementIp?: string,
  port?: number | any,
  snmpCommunityString?: string
}
