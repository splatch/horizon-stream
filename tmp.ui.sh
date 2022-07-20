#!/bin/bash

cd ui/

docker build -t opennms/horizon-stream-ui:local -f ./dev/Dockerfile .

echo 1 > tmp/HS_UI
