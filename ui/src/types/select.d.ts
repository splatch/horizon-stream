import { ISelectItemType } from '@featherds/select/src/components/types'
import { fncArgVoid } from '@/types'

export interface ISelectDropdown {
  label: string,
  options: ISelectItemType[],
  optionText: string,
  cb: fncArgVoid
}
