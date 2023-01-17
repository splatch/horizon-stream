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
  conditions: any[]
}
