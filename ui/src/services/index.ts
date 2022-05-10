import { login, logout, getUserInfo, refreshToken } from './authService'
import { getAlarms, sendAlarm, clearAlarm } from './alarmService'

export default { login, logout, getUserInfo, refreshToken, getAlarms, sendAlarm, clearAlarm }
