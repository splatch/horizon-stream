import casual from 'casual'

casual.define('User', function () {
  return {
    id: casual.uuid,
    name: casual.name,
    password: casual.password
  }
})
casual.define('Users', function () {
  return [casual.User]
})

const user = casual.User
const users = casual.Users

export {
  user,
  users
}
