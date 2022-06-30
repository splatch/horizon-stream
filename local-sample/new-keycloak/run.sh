#!/bin/bash

kind load docker-image keycloak/keycloak:0.0.13
kind load docker-image grafana-test-sso:latest

#openssl req -subj '/CN=localhost/O=Test Keycloak./C=US' -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
#kubectl create secret tls example-tls-secret --cert certificate.pem --key key.pem

openssl req -subj '/CN=keycloak/O=Test Keycloak./C=US' -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
kubectl create secret tls local-dev-tls-secret --cert certificate.pem --key key.pem
rm certificate.pem
rm key.pem

# Install Keycloak
kubectl apply -f secrets.yaml
kubectl apply -f postgres.yaml
sleep 20
kubectl apply -f kc-deployment.yaml
kubectl apply -f kc-service.yaml
kubectl apply -f kc-ingress.yaml

# Install grafana
kubectl apply -f grafana-sso/grafana-sso-b-configmap.yaml
kubectl apply -f grafana-sso/grafana-sso-c-secrets.yaml
kubectl apply -f grafana-sso/grafana-sso-d-deployment.yaml
kubectl apply -f grafana-sso/grafana-sso-e-service.yaml
kubectl apply -f grafana-sso/grafana-sso-f-ingress.yaml

sleep 60

#terraform init && terraform apply -auto-approve

#ACCESS_TOKEN=$(curl -k \
#  -d "client_id=admin-cli" \
#  -d "username=admin" \
#  -d "password=admin" \
#  -d "grant_type=password" \
#  "https://keycloak/auth/realms/master/protocol/openid-connect/token" | jq '.access_token')
#
#RESULT=`curl -k --data "username=admin&password=admin&grant_type=password&client_id=admin-cli" https://keycloak/auth/realms/master/protocol/openid-connect/token`
#ACCESS_TOKEN=`echo $RESULT | sed 's/.*access_token":"//g' | sed 's/".*//g'`
#
## Make sure to change the name of the clientId if exists.
#curl -k -X POST -d '{ "clientId": "client-2" }' -H "Content-Type:application/json" -H "Authorization: bearer $ACCESS_TOKEN" https://keycloak/auth/realms/master/clients-registrations/default
#
## IMPORTANT: Do this quickly after the token, not sure the session length on commandline.
#FILE_NAME=imports/opennms-realm.json
#curl -k -v POST \
#    -H "Authorization: Bearer $ACCESS_TOKEN" \
#    -H "Content-Type: application/json" \
#    -d @"${FILE_NAME}" https://keycloak/auth/admin/realms
#
#curl -k \
#  -H "Authorization: bearer $ACCESS_TOKEN" \
#  "https://keycloak/auth/realms/master"

