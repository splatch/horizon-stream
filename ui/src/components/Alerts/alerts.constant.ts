export enum AlertType {
  CRITICAL,
  MAJOR,
  MINOR,
  WARNING,
  INDETERMINATE
}

export const enum AlertStatus {
  ACKNOWLEDGED,
  UNACKNOWLEDGED
}

export const enum AlertSort {
  DATE,
  TIME,
  NODE_TYPE,
  WATCHING
}
