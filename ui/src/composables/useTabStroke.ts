const useTabStroke = () => {
  onMounted(() => {
    const childForm = document.getElementsByClassName('form')[0]
    if (childForm) {
      const focusables = childForm.querySelectorAll<HTMLElement>(
        'textarea,input,[contenteditable="true"],[type="submit"]'
      )
      console.log(focusables)
      focusables.forEach((el, i) => {
        el.setAttribute('tabindex', (i + 1).toString())
      })
      focusables[0].focus()
    }
  })
}

export default useTabStroke
