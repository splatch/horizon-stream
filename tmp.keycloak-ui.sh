#!/bin/bash

cd keycloak-ui/

docker build -t opennms/horizon-stream-keycloak:local -f ./Dockerfile .

echo 1 > tmp/HS_KEYCLOAK_UI
