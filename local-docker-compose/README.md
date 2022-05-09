# Local Docker Compose

## Overview

This docker-compose configuration exists as an example only.

## Pre-Requisites

* Java 11+
* Docker
* jq

## Steps

    $ docker-compose up

    Starting from the project root:
    $ cd tools
    $ ./KC.login -H localhost:28080 -u keycloak-admin -p admin -R master
    $ ./KC.add-realm -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -R opennms
    $ ./KC.create-user -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -u user001 -p passw0rd -R opennms
    $ ./KC.get-user-by-username -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -u user001 -R opennms | tee data/user.out
    $ jq -r '.[] | .id' data/user.out  | tee data/user-id.txt
    $ ./KC.create-role -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -R opennms -r admin
    $ ./KC.get-realm-roles -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -R opennms | tee data/role.out
    $ jq -r '.[] | select(.["name"] == "admin") | .id' data/role.out | tee data/role-id.txt
    $ ./KC.assign-user-role -H localhost:28080 -r admin -i "$(< data/role-id.txt)" -t "$(< data/ACCESS_TOKEN.txt)" -U "$(< data/user-id.txt)" -R opennms
    $ ./KC.login -H localhost:28080 -u user001 -p passw0rd -R opennms
    $ ./events.list -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
    $ ./events.publish -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
    $ ./events.list -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"

## Run with Kafka

Use the following command in place of `docker-compose up`:

    $ docker-compose -f docker-compose.kafka.yaml up
