#!/bin/bash

printf "\n# Init\n"
printf "################################################################################\n\n"

printf "\n# Clear and remake tmp/ dir"
# Contains files not to be committed to github.

rm -r tmp/
mkdir tmp/

printf "\n# Pull in env vars.\n"

source config-run

echo "$VERSION_KEY_CLOAK"
echo "$DOMAIN_CORE"
echo "$DOMAIN_KARAF"
echo "$DOMAIN_UI"
echo "$DOMAIN_KEYCLOAK"
echo "$DOMAIN_API"

printf "\n# Create Kind cluster\n"
printf "################################################################################\n\n"

kind create cluster --config=config-kind.yaml

printf "\n\n# Confirm connection\n"

kubectl config use-context kind-kind
kubectl config get-contexts

kind load docker-image keycloak/keycloak:0.0.13
kind load docker-image grafana-test-sso:latest

printf "\n# Add Dependencies\n"
printf "################################################################################\n\n"

# Add Dependency - Ingress Nginx
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

printf "\n\n# Update and Deploy yaml files\n"
printf "################################################################################\n\n"



cd ../operator/
bash scripts/install-local.sh
bash scripts/create-instance.sh
cd ../local-sample/

# Wait...
sleep 60

openssl req -subj "/CN=$DOMAIN_KEYCLOAK/O=Test Keycloak./C=US" -newkey rsa:2048 -nodes -keyout tmp/key.pem -x509 -days 365 -out tmp/certificate.pem
kubectl -n local-instance create secret tls tls-cert-wildcard --cert tmp/certificate.pem --key tmp/key.pem

# Wait...
sleep 60

kubectl apply -f services.yaml
#kubectl apply -f tmp/ingress.yaml

# Install Keycloak
kubectl -n local-instance apply -f secrets.yaml
kubectl -n local-instance apply -f postgres.yaml
sleep 20
kubectl -n local-instance apply -f kc-deployment.yaml
kubectl -n local-instance apply -f kc-service.yaml
kubectl -n local-instance apply -f kc-ingress.yaml

printf "\n\n# Grafana\n"
printf "################################################################################\n\n"

kubectl -n local-instance apply -f grafana-a-configmap.yaml
kubectl -n local-instance apply -f grafana-b-secrets.yaml
kubectl -n local-instance apply -f grafana-c-deployment.yaml
kubectl -n local-instance apply -f grafana-d-service.yaml
kubectl -n local-instance apply -f grafana-e-ingress.yaml





# This is only for CI-CD pipeline
if [[ $CI_CD_RUN == true ]]; then

  printf "\n\n# Import Images for Testing \n"
  kind load docker-image opennms/horizon-stream-ui:local
  kind load docker-image opennms/horizon-stream-keycloak:local 
    # This keycloak-ui is referenced from the crd-keycloak.yaml.
  kind load docker-image opennms/horizon-stream-core:local
  kind load docker-image opennms/horizon-stream-rest-server:local
  
  kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui",  "image":"opennms/horizon-stream-ui:local"}]}}}}'
  kubectl patch deployments my-horizon-stream-core -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core","image":"opennms/horizon-stream-keycloak:local"}]}}}}'
  kubectl patch deployments my-horizon-stream-api  -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "image":"opennms/horizon-stream-rest-server:local"}]}}}}'
  kubectl patch deployments my-keycloak            -p '{"spec": {"template": {"spec":{"containers":[{"name": "keycloak",           "image":"opennms/horizon-stream-core:local"}]}}}}'
  
  kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "imagePullPolicy":"Never"}]}}}}'
  kubectl patch deployments my-horizon-stream-core -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core","imagePullPolicy":"Never"}]}}}}'
  kubectl patch deployments my-horizon-stream-api  -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "imagePullPolicy":"Never"}]}}}}'
  kubectl patch deployments my-keycloak            -p '{"spec": {"template": {"spec":{"containers":[{"name": "keycloak",           "imagePullPolicy":"Never"}]}}}}'

else

  kind load docker-image opennms/horizon-stream-keycloak:latest 
  kubectl -n local-instance patch deployments keycloak -p '{"spec": {"template": {"spec":{"containers":[{"name": "keycloak", "imagePullPolicy":"Never"}]}}}}'

  echo # IMPORTANT: If the following do not catch, then update them manually until we can get these integrated.
  kind load docker-image opennms/horizon-stream-ui:0.0.13
  kubectl -n local-instance patch deployments opennms-ui -p '{"spec": {"template": {"spec":{"containers":[{"name": "opennms-ui", "image":"opennms/horizon-stream-ui:0.0.14"}]}}}}'
  kubectl -n local-instance patch deployments opennms-ui -p '{"spec": {"template": {"spec":{"containers":[{"name": "opennms-ui", "imagePullPolicy":"Never"}]}}}}'
  kubectl -n local-instance patch deployments opennms-ui -p '{"spec": {"template": {"spec":{"containers":[{"name": "opennms-ui", "env":[{"name": "DOMAIN_KEYCLOAK", "value":"https://$DOMAIN_KEYCLOAK/auth"}]}]}}}}'
  #kubectl -n local-instance patch deployments opennms-ui -p '{"spec": {"template": {"spec":{"containers":[{"name": "opennms-ui", "env":[{"name": "DOMAIN_API", "value":"https://$DOMAIN_API/"}]}]}}}}'

  # The ingress is not working, just use the default installed one.
  kubectl -n local-instance patch ingress opennms-ingress -p '{"spec":{"ingressClassName":"nginx"}}'

fi

printf "\nWaiting for startup of pods, could take a few minutes\n"
sleep 60

# Wait until services are running.
#kubectl -n local-instance wait --for=condition=ready pod --timeout=600s -l run=my-keycloak
#kubectl -n ingress-nginx wait --for=condition=ready pod --timeout=60s -l app.kubernetes.io/component=controller

printf "\n\n# Create realm, user, and role mappings through keycloak api.\n"
printf "################################################################################\n\n"

./run.kc-config.sh

printf "\n\n# Output\n"
printf "################################################################################\n\n"

printf "\n\nDone\n\nGo to http://$DOMAIN_UI\n\n"

printf "\n\n# TODO\n"
printf "################################################################################\n\n"

printf "\nUpdate the ingress from operator to use the nginx className.\n"
