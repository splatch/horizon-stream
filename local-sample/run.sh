#!/bin/bash

function app_check_wait () {

# For loop to this instead of while loop:
#kubectl wait \
#  --for=condition=ready pod \
#  --selector=app=keycloak-operator

echo "type: $1"
for APP in "$@"; do
  # Ignore the first arg, not an app, but type.
  if [[ $APP != $1 ]]; then
    printf "\nWaiting for $APP to be ready. May take a few minutes.\n Waiting."
    while true; do
      sleep 5; printf "."
      if [[ "$(kubectl get pods -l=$1=$APP -o jsonpath='{.items[*].status.containerStatuses[0].started}')" == "true" ]]; then
        break;
      fi
    done
    printf "\n"
  fi
done

}

printf "\n# Init\n"
printf "################################################################################\n\n"

pringf "\n# Clear and remake tmp/ dir"
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

printf "\n\n# Create Kind cluster\n"
printf "################################################################################\n\n"

kind create cluster --config=config-kind.yaml

printf "\n\n# Confirm connection\n"

kubectl config use-context kind-kind
kubectl config get-contexts

printf "\n\n# Add Dependencies\n"
printf "################################################################################\n\n"

# Add Dependency - Keycloak
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/$VERSION_KEY_CLOAK/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/$VERSION_KEY_CLOAK/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/$VERSION_KEY_CLOAK/kubernetes/kubernetes.yml

# Verify (should get keycloaks and keycloakrealmiiimports)
kubectl api-resources | grep keycloak

# Add Dependency - Ingress Nginx
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

printf "\n\n# Update and Deploy yaml files\n"
printf "################################################################################\n\n"

printf "\n\n# Update yaml files\n"
cat ../dev/kubernetes.kafka.yaml | \
  sed "s/opennms\/horizon-stream-core/opennms\/horizon-stream-core\:latest/" | \
  sed "s/opennms\/horizon-stream-api/opennms\/horizon-stream-rest-server\:latest/" | \
  sed "s/opennms\/horizon-stream-ui-dev/opennms\/horizon-stream-ui\:latest/" | \
  sed "s/frontendUrl: \"http:\/\/localhost:28080\"/frontendUrl: \"http:\/\/$DOMAIN_KEYCLOAK\"/" | \
  sed "s/localhostkey/$DOMAIN_KEYCLOAK/" | \
  sed "s/localhostapi/$DOMAIN_API/" | \
  sed "s/imagePullPolicy: Never/imagePullPolicy: Always/" > tmp/hs.yaml

cat ingress.TEMPLATE.yaml | \
  sed "s/\[\[LOCALHOSTUI\]\]/$DOMAIN_UI/" | \
  sed "s/\[\[LOCALHOSTKEYCLOAK\]\]/$DOMAIN_KEYCLOAK/" | \
  sed "s/\[\[LOCALHOSTKARAF\]\]/$DOMAIN_KARAF/" | \
  sed "s/\[\[LOCALHOSTCORE\]\]/$DOMAIN_CORE/" | \
  sed "s/\[\[LOCALHOSTAPI\]\]/$DOMAIN_API/" > tmp/ingress.yaml

printf "\n\n# Deploy yaml files\n"

kubectl apply -f tmp/hs.yaml

# Wait until services are running.
APPS=('keycloak-operator')
app_check_wait "app.kubernetes.io/name" "${APPS[@]}"
APPS=('my-kafka' 'my-postgres' 'my-keycloak' 'my-horizon-stream-core' 'my-horizon-stream-api' 'my-zookeeper')
app_check_wait "run" "${APPS[@]}"
APPS=('my-horizon-stream-ui')
app_check_wait "app" "${APPS[@]}" 

kubectl apply -f services.yaml
kubectl apply -f tmp/ingress.yaml

printf "\n\n# Create user through keycloak.\n"
printf "################################################################################\n\n"

cd ../tools/
./KC.login -H http://localhostkey/keycloak -u user001 -p passw0rd -R opennms
./events.list -H http://$DOMAIN_CORE -t "$(< data/ACCESS_TOKEN.txt)"
./events.publish -H http://$DOMAIN_CORE -t "$(< data/ACCESS_TOKEN.txt)"
./events.list -H http://$DOMAIN_CORE -t "$(< data/ACCESS_TOKEN.txt)"

printf "\n\n# Output\n"
printf "################################################################################\n\n"

printf "\n\nDone\n\nGo to $DOMAIN_UI and use user 'user001' & password 'passw0rd'\n\n"
