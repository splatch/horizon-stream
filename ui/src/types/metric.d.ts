export interface Chip {
  type?: string,
  label?: string | undefined,
  timestamp?: number | string,
  timeUnit?: number,
  status?: string | boolean,
  value?: string | number | undefined
}