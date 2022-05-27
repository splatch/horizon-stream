#!/usr/bin/env sh

echo $1
echo $2

echo "
VITE_BASE_URL=http://$2
VITE_KEYCLOAK_URL=http://$1
VITE_KEYCLOAK_REALM=opennms
VITE_KEYCLOAK_CLIENT_ID=horizon-stream
" > .env.development

yarn run dev --host
