export interface IPolicy {
  id: string
  name: string
  tags: string[]
  rules: string[]
}

export interface IRule {
  id: string
  name: string
  componentType: string
  detectionMethod: string
  metricName: string
  conditions: ICondition[]
}

export interface ICondition {
  id: number
  level: string
  porcentage: number
  duration: number
  period: number
  severity: string
}
