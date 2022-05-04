import axios from 'axios'
import useSpinner from '@/composables/useSpinner'
import useSnackbar from '@/composables/useSnackbar'
import keycloakConfig from '../../keycloak.config'

const { startSpinner, stopSpinner } = useSpinner()
const { showSnackbar } = useSnackbar()

const login = async (username: string, password: string): Promise<any> => {
  // create endpoint to retrieve token
  const endpoint = `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/token`

  // create encoded params to send
  const params = new URLSearchParams()
  params.append('username', username)
  params.append('password', password)
  params.append('client_id', keycloakConfig.clientId as string)
  params.append('grant_type', 'password')

  startSpinner()
  try {
    const resp = await axios.post(endpoint, params)
    return resp.data
  } catch (err: any) {

    if (err.response.status === 401) {
      showSnackbar({ error: true, msg: 'Unauthorized' })
    }

    return false
  } finally {
    stopSpinner()
  }
}

export { login }
