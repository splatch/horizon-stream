# 4. Create new monitoring policy service

Date: 2023-05-01

## Status

Proposed

## Context

Monitoring policies are a feature that allow the user to configure how alerts are produced. A monitoring policy contains the following:

- The node tags that the monitoring policy should create alerts for.
- How network operators are notified about the monitoring policy's alerts.
- Rules to create alerts for single SNMP traps or internal events.
- Rules to create alerts based on thresholds for occurrences of an SNMP trap or internal event over a time window.
- Rules to create alerts based on thresholds for metrics or flows.
- How alerts are cleared.
- The severity level of alerts.
- Multiple levels of conditions for thresholds.
- Severity level for each condition.

There are multiple components that use parts of monitoring policies:

- The alert engine is configured using rules, conditions, thresholds, events, and severities from the policy.
- Alerts contain references to the monitoring policy and the rule that it was created from.
- The upcoming thresholding service will use threshold conditions from monitoring policies.
- The Notification service uses notification configuration from monitoring policies.

Monitoring policies are currently implemented in the Alert service. This has the following concerns:

- The alert engine cannot scale independently of monitoring policy configuration.
- The alert engine only needs event UEIs and associated reduction/clear keys. Monitoring policies are associated with many different features across the system, so there are issues with cohesiveness:
  - Notification and threshold configuration are used by other services.
  - Monitoring policies need knowledge of internal events, SNMP traps, individual metric names, and flows.
  - Synthetic transactions may be implemented alongside monitoring policies, bringing in even more information.

Synthetic transactions are very similar to monitoring policies, but contain more Inventory knowledge and will directly trigger data collection tasks for Minions. The current requirements for alert-related configuration like rules and notifications are currently very similar or the same. The similarity of synthetic transactions to monitoring policies means we may want to include the two in the same microservice. The added responsibilities would likely be inappropriate to add to the Alert service.

## Decision

We will create a new "Monitoring Policy" microservice, and move monitoring policies to it from the Alert service.

Monitoring policy configuration will be communicated to other microservices asynchronously with Kafka.

Asynchronous communication will be done using system events that express domain events, e.g. "MetricThresholdConfigured".

The Alert service, Notification service, and upcoming Threshold service will consume the domain events produced by the Monitoring Policy service.

## Consequences

- The Alert service will be more focused on managing alerts and the alert engine, increasing its cohesion.
- The alert engine and API will be capable of scaling independently of monitoring policies.
- It becomes very obvious where monitoring policies exist.
- Monitoring policies, tags, rules, and the events configured within monitoring policies or produced for thresholds will need to be communicated to the alert service.
- The alert engine will lose high consistency in favour of eventual consistency.
- The Alert service will still need the UEIs and reduction/clear keys for each event it uses to produce or clear alerts. This needs to be designed.
- A new microservice needs to be developed, deployed, and maintained. This includes new devops pipelines, a SonarCloud project, Kubernetes objects, local development configuration, a database, etc.
