#!/usr/bin/env bash
set -e


kindConfig='dev/tmp/kind-config.yaml'
keycloakVersion='18.0.0'

# Get location of local Maven repository
if [ -n "${MVN_REPO}" ];
then
  echo "Maven repository location set through MVN_REPO variable: $MVN_REPO"
else
  echo "No MVN_REPO variable set, pulling location from Maven."
  MVN_REPO=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
  echo "Maven repository location found: $MVN_REPO"
fi

# Create a kind-config file with the local Maven repository mounted onto the control plane
mkdir -p dev/tmp
sed "s/{{MVN_REPO}}/$(printf '%s\n' "$MVN_REPO" | sed 's/[\/&]/\\&/g')/" dev/kind-config-template.yaml > $kindConfig
echo "Generated new Kind config file: $kindConfig"

# Create the cluster using the generated kind-config file
echo "Creating cluster using Kind with settings from $kindConfig"
kind create cluster --config $kindConfig

# Apply Keycloak CRDs to cluster
echo "Applying Keycloak CRDs to cluster"
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/$keycloakVersion/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/$keycloakVersion/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/$keycloakVersion/kubernetes/kubernetes.yml

echo "Horizon Stream development cluster ready - try 'skaffold dev'!"
