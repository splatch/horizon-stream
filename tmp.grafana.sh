#!/bin/bash

cd grafana/

docker build -t opennms/horizon-stream-grafana:local -f ./Dockerfile .

echo 1 > tmp/HS_GRAFANA
