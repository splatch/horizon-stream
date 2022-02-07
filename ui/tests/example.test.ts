import { test } from 'uvu'
import * as assert from 'uvu/assert'

test('Tests are running', () => {
  assert.equal(2 + 2, 4)
})

test.run()
