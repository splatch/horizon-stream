// import { mount } from '@vue/test-utils'
import mountWithPiniaVillus from 'tests/mountWithPiniaVillus'

import AlertsCardList from '@/components/Alerts/AlertsCardList.vue'

let wrapper: any

describe('Alerts list', () => {
  beforeAll(() => {
    wrapper = mountWithPiniaVillus({
      component: AlertsCardList,
      shallow: true
    })
  })

  test('Mount', () => {
    expect(wrapper).toBeTruthy()
  })

  test('Should have select all checkbox', () => {
    const elem = wrapper.get('[data-test="select-all-checkbox"]')
    expect(elem.exists()).toBeTruthy()
  })

  test('Should have clear button, disabled, enabled if alert selected', async () => {
    const elem = wrapper.get('[data-test="clear-btn"]')
    expect(elem.exists()).toBeTruthy()
    expect(elem.attributes('disabled')).toBeTruthy()
    // TODO: disabled state not working
    // const selectAll = wrapper.get('[data-test="select-all-checkbox"]')
    // await selectAll. setChecked()
    // wrapper.vm.$nextTick()
    // console.log('>>>', selectAll.element.checked)
    // console.log('>>>', elem.attributes('disabled'))
    // expect(elem.attributes('disabled')).toBeFalsy()
  })

  test('Should have aclnowledge selected button, disabled, enabled if alert selected', async () => {
    const elem = wrapper.get('[data-test="acknowledge-btn"]')
    expect(elem.exists()).toBeTruthy()
    expect(elem.attributes('disabled')).toBeTruthy()
    // TODO: disabled state not working
  })

  test('Should have list count (top)', () => {
    const elem = wrapper.get('[data-test="pagination-top"]')
    expect(elem.exists()).toBeTruthy()
  })

  test('Should display a list if have alerts', () => {
    expect(false).toBeTruthy()
  })

  test('Should display empty message if no alerts', () => {
    expect(false).toBeTruthy()
  })

  test('Should have pagination (bottom)', () => {
    const elem = wrapper.get('[data-test="pagination-bottom"]')
    expect(elem.exists()).toBeTruthy()
  })
})
