import { AppContext, createVNode, render } from "vue"

const addWidget = (
  appContext: AppContext,
  widget: string,
  element: HTMLElement,
  props: {[x: string] : string} = {}, 
  styles?: { height: string }
  ) => {
    
  const component = defineAsyncComponent(() => import( /* @vite-ignore */ widget))
  const div = document.createElement('div')
  div.id = widget

  if (styles) {
    div.style.height = styles.height
  }

  const vNode = createVNode(component, props)
  vNode.appContext = appContext
  render(vNode, div)
  element.appendChild(div)
}

const removeWidget = (widget: HTMLElement | string) => {
  let div: HTMLElement

  if (typeof widget === 'string') {
    div = document.getElementById(widget) as HTMLDivElement
  } else {
    div = widget
  }

  render(null, div)
  div.remove()
}

export {
  addWidget,
  removeWidget
}
