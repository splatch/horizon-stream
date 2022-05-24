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
    echo "Waiting for $APP to be ready. May take a few minutes."
    while true; do
      sleep 5; echo "Waiting..."
      if [[ "$(kubectl get pods -l=$1=$APP -o jsonpath='{.items[*].status.containerStatuses[0].started}')" == "true" ]]; then
        break;
      fi
    done
  fi
done

}

# Clear and remake tmp/ dir. Contains files not to be committed to github.
rm -r tmp/
mkdir tmp/

# Create Kind cluster
kind create cluster --config=config.yaml

# Confirm connection
kubectl config use-context kind-kind
kubectl config get-contexts

# Add Dependency - Keycloak
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/kubernetes.yml

# Verify (should get keycloaks and keycloakrealmiiimports)
kubectl api-resources | grep keycloak

# Add Dependency - Ingress Nginx
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

# Update local build from dev
cat ../dev/kubernetes.kafka.yaml | \
  sed 's/opennms\/horizon-stream-core/opennms\/horizon-stream-core\:latest/' | \
  sed 's/opennms\/horizon-stream-api/opennms\/horizon-stream-rest-server\:latest/' | \
  sed 's/opennms\/horizon-stream-ui-dev/opennms\/horizon-stream-ui\:latest/' | \
  sed 's/frontendUrl: \"http:\/\/localhost:28080\"/frontendUrl: \"http:\/\/localhostkey\"/' | \
  sed 's/imagePullPolicy: Never/imagePullPolicy: Always/' > tmp/hs.yaml

# Deploy HS
kubectl apply -f tmp/hs.yaml

# Wait until services are running.
APPS=('keycloak-operator')
app_check_wait "app.kubernetes.io/name" "${APPS[@]}"
APPS=('my-kafka' 'my-postgres' 'my-keycloak' 'my-horizon-stream-core' 'my-horizon-stream-api' 'my-zookeeper')
app_check_wait "run" "${APPS[@]}"
APPS=('my-horizon-stream-ui')
app_check_wait "app" "${APPS[@]}" 

# Confirm Deployment
kubectl get all

kubectl apply -f services.yaml
kubectl apply -f ingress.yaml

# Implement port-forwarding to relevant services.
#nohup kubectl port-forward deployment.apps/my-horizon-stream-ui   30000:3000 &> tmp/hs-ui.log &
#nohup kubectl port-forward deployment.apps/my-horizon-stream-api  29090:9090 &> tmp/hs-api.log &
#nohup kubectl port-forward deployment.apps/my-horizon-stream-core 18181:8181 &> tmp/hs-core.log &
#nohup kubectl port-forward deployment.apps/my-horizon-stream-core 18101:8101 &> tmp/hs-core.log &
#nohup kubectl port-forward deployment.apps/my-keycloak            28080:8080 &> tmp/keycloak.log &

# Create user through keycloak.
cd ../tools/
./KC.login -H http://localhostkey/keycloak -u user001 -p passw0rd -R opennms
#./events.list -H localhost/core -t "$(< data/ACCESS_TOKEN.txt)"
#./events.publish -H localhost/core -t "$(< data/ACCESS_TOKEN.txt)"
#./events.list -H localhost/core -t "$(< data/ACCESS_TOKEN.txt)"

printf "\n\nDone\n\nGo to localhost and use user 'user001' & password 'passw0rd'\n\n"
