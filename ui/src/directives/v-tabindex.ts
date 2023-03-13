/**
 * Add `v-tabindex` to the parent of inputs
 */
const tabIndexDirective = {
  mounted(el: Element) {
    const focusables = el.querySelectorAll<HTMLElement>('textarea,input,[contenteditable="true"],[type="submit"]')
    focusables.forEach((el, i) => {
      el.setAttribute('tabindex', (i + 1).toString())
    })
    focusables[0].focus()
  }
}

export default tabIndexDirective
