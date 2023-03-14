import { mount } from '@vue/test-utils'
import ACardList from '@/components/Alerts/ACardList.vue'

let wrapper: any

describe('Alerts list', () => {
  beforeAll(() => {
    wrapper = mount(ACardList, {
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
    // const selectAll = wrapper.get('[data-test="select-all-checkbox"]')
    // await selectAll. setChecked()
    // wrapper.vm.$nextTick()
    // console.log('>>>', selectAll.element.checked)
    // console.log('>>>', elem.attributes('disabled'))
    // expect(elem.attributes('disabled')).toBeFalsy()
  })

  test('Should have list count (top)', () => {
    const elem = wrapper.get('[data-test="list-count-top"]')
    expect(elem.exists()).toBeTruthy()
  })

  test('Should display a list if have alerts', () => {
    expect(false).toBeTruthy()
  })

  test('Should display empty message if no alerts', () => {
    expect(false).toBeTruthy()
  })

  test('Should have row per page', () => {
    expect(false).toBeTruthy()
  })

  test('Should have list count (bottom)', () => {
    const elem = wrapper.get('[data-test="list-count-bottom"]')
    expect(elem.exists()).toBeTruthy()
  })

  test('Should have pagination', () => {
    expect(false).toBeTruthy()
  })
})
