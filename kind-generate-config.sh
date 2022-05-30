#!/usr/bin/env bash

if [ -n "${MVN_REPO}" ];
then
  echo "Maven repository location set through MVN_REPO variable: ${MVN_REPO}"
else
  echo "No MVN_REPO variable set, pulling location from Maven."
  MVN_REPO=$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
  echo "Maven repository location found: ${MVN_REPO}"
fi

sed "s/{{MVN_REPO}}/$(printf '%s\n' "$MVN_REPO" | sed 's/[\/&]/\\&/g')/" kind-config-template.yaml > kind-config.yaml
echo "Generated new Kind config file: kind-config.yaml"
