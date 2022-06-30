#!/bin/bash

# The problem with this method is that the resulting token is surrounded by double quotes and does not work.
ACCESS_TOKEN=$(curl -k \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  "https://keycloak/auth/realms/master/protocol/openid-connect/token" | jq '.access_token')

# This method removes the quotes and works.
RESULT=`curl -k --data "username=admin&password=admin&grant_type=password&client_id=admin-cli" https://keycloak/auth/realms/master/protocol/openid-connect/token`
ACCESS_TOKEN=`echo $RESULT | sed 's/.*access_token":"//g' | sed 's/".*//g'`

# Make sure to change the name of the clientId if exists.
#curl -k -X POST -d '{ "clientId": "client-2" }' -H "Content-Type:application/json" -H "Authorization: bearer $ACCESS_TOKEN" https://keycloak/auth/realms/master/clients-registrations/default

# IMPORTANT: Do this quickly after the token, not sure the session length on commandline.
FILE_NAME=imports/test-opennms-realm.json

# Create Realm
curl -k -v POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d @"${FILE_NAME}" https://keycloak/auth/admin/realms

curl -k \
  -H "Authorization: bearer $ACCESS_TOKEN" \
  "https://keycloak/auth/realms/master"

curl -k -v \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://keycloak/auth/admin/realms/opennms/users/3e6b5751-1b01-45f4-ab8d-6072aa6d2914

# Get roles for client 'account'. The id in client UI field 'Client ID' is
# 'account', but in the UI, going to client 'account', I could see the actual
# id for it in the URL.
curl -k -v \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://keycloak/auth/admin/realms/opennms/clients/049dbe38-98a6-4034-8e82-f46bbeb008a1/roles

curl -k -v POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"firstName":"user","lastName":"001", "email":"user001@test.com", "enabled":"true", "username":"user001"}' \
    https://keycloak/auth/admin/realms/opennms/users

# IMPORTANT: Get the role IDs from the above query to the account /roles and get out IDs using jq.
curl -k -v POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d '[ { "id": "78956cd9-2d98-4eef-adf2-9224ed95ba27", "name": "manage-account" }, { "id": "e4600129-ed11-4d1c-8a3c-b4dda1952246", "name": "view-profile" } ]' \
    https://keycloak/auth/admin/realms/opennms/users/3e6b5751-1b01-45f4-ab8d-6072aa6d2914/role-mappings/clients/049dbe38-98a6-4034-8e82-f46bbeb008a1
  
# TODO
# - Dynamically add IDs above from queries to keycloak using curl and jq.
# - Update admin user under opennms realm as well.
