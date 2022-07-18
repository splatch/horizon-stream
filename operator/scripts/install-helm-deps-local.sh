#!/usr/bin/env bash

helm upgrade -i operator-deps-local ../charts/opennms-operator-dependencies -f values.yaml