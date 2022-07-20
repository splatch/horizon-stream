#!/bin/bash

cd rest-server/

mvn clean install jib:dockerBuild -Dimage=opennms/horizon-stream-rest-server:local

echo 1 > tmp/HS_REST_SERVER
