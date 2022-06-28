#!/bin/bash

kind load docker-image keycloak/keycloak:0.0.13
kind load docker-image grafana-test-sso:latest

#openssl req -subj '/CN=localhost/O=Test Keycloak./C=US' -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
#kubectl create secret tls example-tls-secret --cert certificate.pem --key key.pem

# Install Keycloak
kubectl apply -f namespace.yaml
kubectl apply -f secrets.yaml
kubectl apply -f postgres.yaml
sleep 20
kubectl apply -f pods-and-services.yaml
kubectl apply -f ingress.keycloak.yaml

# Install grafana
kubectl apply -f grafana-sso/grafana-sso-b-configmap.yaml
kubectl apply -f grafana-sso/grafana-sso-c-secrets.yaml
kubectl apply -f grafana-sso/grafana-sso-d-deployment.yaml
kubectl apply -f grafana-sso/grafana-sso-e-service.yaml
kubectl apply -f grafana-sso/grafana-sso-f-ingress.yaml

terraform init && terraform apply -auto-approve
