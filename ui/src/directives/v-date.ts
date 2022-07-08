import { computed } from 'vue'
import { format as fnsFormat } from 'date-fns-tz'
import { parseISO } from 'date-fns'

const timeZone = computed<string>(() => Intl.DateTimeFormat().resolvedOptions().timeZone)
const formatString = 'yyyy-MM-dd\'T\'HH:mm:ssxxx'

const dateFormatDirective = {
  mounted(el: Element) {
    if (!el.innerHTML) return
    const date = Number(el.innerHTML) || parseISO(el.innerHTML)
    if (!date) return
    const formattedDate = fnsFormat(date, formatString, { timeZone: timeZone.value })
    el.innerHTML = formattedDate
  }
}

export default dateFormatDirective
