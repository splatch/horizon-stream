#!/usr/bin/env bash

if [ -n "${MVN_REPO}" ];
then
  echo "Maven repository location set through MVN_REPO variable: ${MVN_REPO}"
else
  echo "No MVN_REPO variable set, pulling location from Maven."
  MVN_REPO=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
  echo "Maven repository location found: ${MVN_REPO}"
fi

sed "s/{{MVN_REPO}}/$(printf '%s\n' "$MVN_REPO" | sed 's/[\/&]/\\&/g')/" dev/kind-config-template.yaml > dev/tmp/kind-config.yaml
echo "Generated new Kind config file: dev/tmp/kind-config.yaml"

echo "Creating cluster using Kind"
kind create cluster --config dev/tmp/kind-config.yaml

echo "Applying Keycloak Operator"
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/kubernetes.yml
