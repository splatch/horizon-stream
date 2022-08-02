#!/usr/bin/env bash

echo
echo ____________Waiting for Local Instance_____________
echo

until kubectl wait --for=jsonpath='{.status.phase}'=Active namespace/local-instance --timeout=30s 2> /dev/null
do
    sleep 2
    echo Waiting for local-instance namespace....
done
if [ $? -ne 0 ]; then exit; fi
echo local-instance namespace found


echo
echo _________Creating and Applying SSL Cert____________
echo

mkdir tmp

openssl req -subj "/CN=onmshs/O=Test Keycloak./C=US" -newkey rsa:2048 -nodes -keyout tmp/key.pem -x509 -days 365 -out tmp/certificate.pem
if [ $? -ne 0 ]; then exit; fi
kubectl -n local-instance create secret tls tls-cert-wildcard --cert tmp/certificate.pem --key tmp/key.pem
if [ $? -ne 0 ]; then exit; fi
