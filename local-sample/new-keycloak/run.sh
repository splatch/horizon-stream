#!/bin/bash

kind load docker-image keycloak/keycloak:0.0.13
kind load docker-image grafana-test-sso:latest

#openssl req -subj '/CN=localhost/O=Test Keycloak./C=US' -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
#kubectl create secret tls example-tls-secret --cert certificate.pem --key key.pem

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
ACCESS_TOKEN=$(curl -k -X POST \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  "https://localhost/auth/realms/master/protocol/openid-connect/token" | jq '.access_token')
curl -k -v POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d "@imports/opennms-realm.json" https://localhost/auth/admin/realms
