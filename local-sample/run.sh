#!/bin/bash

printf "\n# Init\n"
printf "################################################################################\n\n"

printf "\n# Clear and remake tmp/ dir"
# Contains files not to be committed to github.

rm -r tmp/
mkdir tmp/

printf "\n# Pull in env vars.\n"

# TODO - Put all secrets in hear as well.
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

printf "\n# Add Dependencies\n"
printf "################################################################################\n\n"

# Add Dependency - Ingress Nginx
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

printf "\n\n# Update and Deploy yaml files\n"
printf "################################################################################\n\n"

# TODO: Remove the /.m2 persistent volume and volume claim from the manifest
# yaml file. This is for skaffold and will cause this to fail.
# Just remove the following volume mount:
#            - name: maven-repo-volume
#              mountPath: "/opt/horizon-stream/.m2"
#              readOnly: true

printf "# Update yaml files\n"
cat ../dev/kubernetes.kafka.yaml | \
  sed "s/opennms\/horizon-stream-core/opennms\/horizon-stream-core\:$IMAGE_TAG/" | \
  sed "s/opennms\/horizon-stream-api/opennms\/horizon-stream-rest-server\:$IMAGE_TAG/" | \
  sed "s/opennms\/horizon-stream-ui-dev/opennms\/horizon-stream-ui\:$IMAGE_TAG/" | \
  sed "s/opennms\/horizon-stream-keycloak-dev/opennms\/horizon-stream-keycloak\:$IMAGE_TAG/" | \
  sed "s/frontendUrl: \"http:\/\/localhost:28080\"/frontendUrl: \"http:\/\/$DOMAIN_KEYCLOAK\"/" | \
  sed "s/localhost:28080/https:\/\/$DOMAIN_KEYCLOAK\/auth/" | \
  sed "s/localhost:48080/http:\/\/$DOMAIN_API/" | \
  sed "s/keycloak-admin/admin/" | \
  sed "s/imagePullPolicy: Never/imagePullPolicy: Always/" > tmp/hs.yaml

cat ingress.TEMPLATE.yaml | \
  sed "s/\[\[LOCALHOSTUI\]\]/$DOMAIN_UI/" | \
  sed "s/\[\[LOCALHOSTKEYCLOAK\]\]/$DOMAIN_KEYCLOAK/" | \
  sed "s/\[\[LOCALHOSTKARAF\]\]/$DOMAIN_KARAF/" | \
  sed "s/\[\[LOCALHOSTCORE\]\]/$DOMAIN_CORE/" | \
  sed "s/\[\[LOCALHOSTAPI\]\]/$DOMAIN_API/" > tmp/ingress.yaml

printf "\n\n# Deploy yaml files\n"

kubectl apply -f tmp/hs.yaml

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
  kubectl patch deployments my-keycloak            -p '{"spec": {"template": {"spec":{"containers":[{"name": "keycloak",           "imagePullPolicy":"Never"}]}}}}'
  
  # TODO: Once these images are published and stable, need to remove some of the
  # following.
  
  # Need to put these on our Dockerhub account.
  kind load docker-image keycloak/keycloak:0.0.13
  kind load docker-image grafana-test-sso:latest

  kind load docker-image opennms/horizon-stream-core:local
  kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "image":"opennms/horizon-stream-core:local"}]}}}}'
  kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "imagePullPolicy":"Never"}]}}}}'
  kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "env":[{"name": "KEYCLOAK_BASE_URL", "value":"https://keycloak:8443/auth"}]}]}}}}'
  kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "env":[{"name": "KEYCLOAK_ADMIN_USERNAME", "value":"admin"}]}]}}}}'
  
  kind load docker-image opennms/horizon-stream-rest-server:local
  kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "image":"opennms/horizon-stream-rest-server:local"}]}}}}'
  kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "imagePullPolicy":"Never"}]}}}}'
  kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "env":[{"name": "KEYCLOAK_AUTH_SERVER_URL", "value":"https://keycloak:8443/auth"}]}]}}}}'
  kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "env":[{"name": "HORIZON_STREAM_KEYCLOAK_ADMIN_USERNAME", "value":"admin"}]}]}}}}'
  
  kind load docker-image opennms/horizon-stream-ui:local
  kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "image":"opennms/horizon-stream-ui:local"}]}}}}'
  kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "imagePullPolicy":"Never"}]}}}}'
  kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_KEYCLOAK", "value":"https://$DOMAIN_KEYCLOAK/auth"}]}]}}}}'
  #kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_API", "value":"https://$DOMAIN_KEYCLOAK/api"}]}]}}}}'
  kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_API", "value":"https://$DOMAIN_API/"}]}]}}}}'

fi

printf "\nWaiting for startup of pods, could take a few minutes\n"

# Let creation of pods
sleep 30

# Wait until services are running.
kubectl wait --for=condition=ready pod --timeout=600s -l run=my-horizon-stream-core
kubectl wait --for=condition=ready pod --timeout=600s -l run=my-horizon-stream-api
kubectl wait --for=condition=ready pod --timeout=600s -l run=my-kafka
kubectl wait --for=condition=ready pod --timeout=600s -l run=my-postgres
kubectl wait --for=condition=ready pod --timeout=60s -l run=my-keycloak
kubectl wait --for=condition=ready pod --timeout=60s -l run=my-zookeeper
kubectl wait --for=condition=ready pod --timeout=600s -l app=my-horizon-stream-ui
kubectl wait --for=condition=ready pod --timeout=600s -l app.kubernetes.io/name=keycloak-operator
kubectl -n ingress-nginx wait --for=condition=ready pod --timeout=60s -l app.kubernetes.io/component=controller

# Wait...
sleep 60

# Old ones, delete
kubectl delete service/keycloak
kubectl delete deployment.apps/my-keycloak

kubectl apply -f services.yaml
kubectl apply -f tmp/ingress.yaml
 
# There are issues with this until we get to stable version.
kubectl delete KeycloakRealmImport opennms

# There are issues with this until we get to stable version.
#kubectl apply -f crd-keycloakrealmimport.yaml

# Wait...
sleep 60
 
printf "\n\n# KeyCloak\n"
printf "################################################################################\n\n"

openssl req -subj "/CN=$DOMAIN_KEYCLOAK/O=Test Keycloak./C=US" -newkey rsa:2048 -nodes -keyout tmp/key.pem -x509 -days 365 -out tmp/certificate.pem
kubectl create secret tls local-dev-tls-secret --cert tmp/certificate.pem --key tmp/key.pem

# Install Keycloak
kubectl apply -f secrets.yaml
kubectl apply -f postgres.yaml
sleep 20
kubectl apply -f kc-deployment.yaml
kubectl apply -f kc-service.yaml
kubectl apply -f kc-ingress.yaml


printf "\n\n# Grafana\n"
printf "################################################################################\n\n"

kubectl apply -f grafana-a-configmap.yaml
kubectl apply -f grafana-b-secrets.yaml
kubectl apply -f grafana-c-deployment.yaml
kubectl apply -f grafana-d-service.yaml
kubectl apply -f grafana-e-ingress.yaml

printf "\n\n# Create user through keycloak.\n"
printf "################################################################################\n\n"

./run.create-realm.sh 

printf "\n\n# Output\n"
printf "################################################################################\n\n"

printf "\n\n****************************************** \n"
printf "*************** IMPORTANT **************** \n"
printf "****************************************** \n"
printf "  Run ./run.create-realm.sh.\n"
printf "  See ISSUE.integrating_new_keycloak.md until gets integrated.\n"
printf "  Done\n"
printf "  Go to https://$DOMAIN_UI/ui/, IMPORTANT, make sure the trailing slash is there or else it does not work, TODO item. \n"
