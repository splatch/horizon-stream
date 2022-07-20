#!/bin/bash

cd platform/

mvn -Prun-it -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 clean install

echo 1 > tmp/HS_CORE
