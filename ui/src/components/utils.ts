import { add, intervalToDuration, formatDuration } from 'date-fns'
import { TimeUnit } from '@/types'

/**
 * 
 * @param timestamp 
 * @param timeUnit 
 * @returns 
 */
export const getHumanReadableDuration = (timestamp: number, timeUnit = TimeUnit.Secs) => {
  let durationDisplay = '--' // undefined | null

  if(![undefined, null].includes(timestamp as any)) {
    const duration = intervalToDuration({
      start: new Date(),
      end: add(new Date(), {seconds: timeUnit === TimeUnit.Secs ? timestamp : timestamp / 1000})
    })
      
    durationDisplay = formatDuration(duration, { format: ['days', 'hours', 'minutes']})
  }

  return durationDisplay
}
