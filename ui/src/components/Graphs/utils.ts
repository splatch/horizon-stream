import { fromUnixTime, format } from 'date-fns'

/**
 * 
 * @param timestamp in milliseconds
 * @param formatStr 
 * @returns 
 */
export const formatTimestamp = (timestamp: number, formatStr: string) => {
  const date = fromUnixTime(timestamp)

  switch (formatStr) {
    case 'mmss':
      return format(date, 'mm:ss')
    case 'hh':
      return format(date, 'HH')
    case 'minutes':
      return format(date, 'HH:mm:ss')
    case 'hours':
      return format(date, 'HH:mm')
    case 'days':
      return format(date, 'dd/MMM HH:mm')
    case 'months':
      return format(date, 'dd/MMM')
    case 'years':
      return format(date, 'MMM/y')
    default:
      return format(date, 'dd/MMM :HH:mm')
  }
}
