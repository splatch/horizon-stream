import { Commit, Dispatch } from 'vuex'

export interface VuexContext {
  commit: Commit
  dispatch: Dispatch
}
