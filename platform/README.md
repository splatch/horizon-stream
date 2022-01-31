# Platform

Currently in proof-of-concept phase

## General approach so far

* Start over with a new module tree
* Use a BOM for managing dependencies
* Revised naming pattern for modules
* Only keep units that work with limited changes
* Delete all ITs and Spring wiring
* Rewrite wiring in Blueprint
* Combine APIs into domains instead of function i.e. (events/api vs opennms-dao-api)
* Rename packages as necessary
* Create new Karaf feature files, pull from existing as needed
* Use the `karaf-maven-plugin` to verify the Karaf features

## Must do

* Split Minion & Core
** Figure out best way to setup the features - minimize the code loaded on Minion
* Model definition, validation & lifecycle
** State vs configuration vs limits & tuning
** Common model (OnmsNode) vs domain specific (OnmsAlarm)
** Bundle organization & DAO services
* Authentication & permission model
** Use standalone auth server, and JWTs
* Managing secrets
* Pull in CM engine
* Observability: OpenMetrics

## Nice to do

* Map our custom criteria classes to JPA?
* Move to JDK 17

## Testing on Karaf

```
feature:repo-add mvn:org.opennms.horizon.assemblies/features/0.1.0-SNAPSHOT/xml/features
feature:install aries-blueprint horizon-icmp
opennms:ping -l cloud 127.0.0.1
```

```
feature:repo-add mvn:org.opennms.horizon.assemblies/features/0.1.0-SNAPSHOT/xml/features
feature:install aries-blueprint horizon-alarms
```

```
curl -v -u admin:admin http://localhost:8181/alarms/list```
```

