#!/usr/bin/env bash

bash scripts/install-operator-os.sh

bash scripts/create-instance.sh

kubectl config set-context --current --namespace=local-instance
