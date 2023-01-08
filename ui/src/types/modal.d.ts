export interface ModalAction {
  label: string
  handler: object
}

export interface ModalDelete {
  title: string
  cssClass: string
  content: string
  minionId: number | null
  action: {
    cancel: ModalAction
    save: ModalAction
  }
  hideTitle: boolean
}
