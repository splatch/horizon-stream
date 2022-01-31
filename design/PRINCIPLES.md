
# Testing

Write unit tests that cover specific classes and mock its dependencies

Avoid grey box testing and writing tests with complex wiring of dependencies

Favor black box testing w/ BDD for communicating test results

# APIs

Provide comprehensive OpenAPI specs

Use DTOs for external objects - don't reuse database entity objects

# What to pull from OpenNMS?

We want to leverage the existing OpenNMS codebase as much as we can!

What are the best pieces to keep?

* ICMP
* SNMP
* Telemetryd
* Pipelines, protocols
* Plugins, connectors, APIs
* IPC
* Parts of provisioning

What pieces do we want to rethink?

* Model
* DAO
* Persistence for configuration & state
* Web & UI
* Authorization
* Scheduling
* Event persistence
    * Performance bottleneck

