#!/usr/bin/env bash

set -euo pipefail
trap 's=$?; echo >&2 "$0: Error on line "$LINENO": $BASH_COMMAND"; exit $s' ERR

if [ $# -ge 1 ] && [ "$1" == "-f" ]; then
    force=1
    shift
else
    force=0
fi

namespace="$1"
domain="$2"

secret="$3"
caKeyFile="$4"
caCrtFile="$5"

if [ ! -f $caKeyFile ]; then
  echo "Required file $caKeyFile does not exist!"
  exit 1
fi

directory=$(dirname $caKeyFile)
id=$(uuidgen)

# see if the secret already exists
if [ -n "$(kubectl -n $namespace get --ignore-not-found=true secret "$secret")" ]; then
  if [ $force -gt 0 ]; then
    kubectl -n $namespace delete "secrets/$secret"
  else
    echo "Secret '$secret' already exists, not adding. Use '-f' option to force recreation." >&2
    exit 0
  fi
fi

openssl req -newkey rsa:2048 -nodes -keyout "$directory/$id.key" -subj "/CN=$domain/O=Test/C=US" -out "$directory/$id.csr"
openssl x509 -req -extfile <(printf "subjectAltName=DNS:$domain") -days 14 -in  "$directory/$id.csr" \
  -CA $caCrtFile -CAkey $caKeyFile -CAcreateserial -out "$directory/$id.crt" || (echo "Certificate $id signing failed" && exit 1)
kubectl -n $namespace create secret tls $secret --cert "$directory/$id.crt" --key "$directory/$id.key"

rm "$directory/$id.crt" "$directory/$id.key" "$directory/$id.csr"
