import casual from 'casual'

export const rndNumber = () => Math.floor(Math.random() * 100)

export const rndStatus = () => casual.random_element(['UP', 'DOWN'])

export const rndLatency = () => Math.floor(Math.random() * 150)

export const rndUptime = () => Math.floor(Math.random() * 200)