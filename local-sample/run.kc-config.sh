#!/bin/bash

# TODO: Add these to config-run.
MASTER_REALM_ADMIN_USER_NAME=admin
MASTER_REALM_ADMIN_PW=admin
ADMIN_USER_NAME=admin
ADMIN_USER_PW=admin
ADMIN_USER_EMAIL=admin@test.com
USER_NAME=user001
USER_PW=test001
USER_EMAIL=test001@test.com

# Gets token and removes double quotes.
# The session length (life span) of this token is not very long.
ACCESS_TOKEN=$(curl -sk \
  -d "client_id=admin-cli" \
  -d "username=$MASTER_REALM_ADMIN_USER_NAME" \
  -d "password=$MASTER_REALM_ADMIN_PW" \
  -d "grant_type=password" \
  "https://onmshs/auth/realms/master/protocol/openid-connect/token" | jq '.access_token' | sed 's/"//g')

################################################################################
printf "\n# Add Realm\n"
################################################################################

# IMPORTANT: Do this quickly after the token, not sure the session length on commandline.
FILE_NAME=./local-sample/imports/test-opennms-realm.json

# Create Realm
curl -k POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d @"${FILE_NAME}" https://onmshs/auth/admin/realms

# Get realm info.
#curl -k \
#  -H "Authorization: bearer $ACCESS_TOKEN" \
#  "https://onmshs/auth/realms/opennms" | jq

################################################################################
printf "\n# Create Users\n"
################################################################################

curl -sk POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"firstName\":\"\",\"lastName\":\"\", \"email\":\"$USER_EMAIL\", \"enabled\":\"true\", \"username\":\"$USER_NAME\",\"credentials\":[{\"type\":\"password\",\"temporary\":false,\"value\":\"$USER_PW\"}]}" \
    https://onmshs/auth/admin/realms/opennms/users

curl -sk POST \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"firstName\":\"\",\"lastName\":\"\", \"email\":\"$ADMIN_USER_EMAIL\", \"enabled\":\"true\", \"username\":\"$ADMIN_USER_NAME\",\"credentials\":[{\"type\":\"password\",\"temporary\":false,\"value\":\"$ADMIN_USER_PW\"}]}" \
    https://onmshs/auth/admin/realms/opennms/users

###a#############################################################################
printf "\n# Update User Client Roles\n"
################################################################################

USER_ID=$(curl -ks \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://onmshs/auth/admin/realms/opennms/users | jq ".[] | select(.username==\"$ADMIN_USER_NAME\").id" | sed 's/"//g')

printf "\nUSER_ID: $USER_ID\n"

for CLIENT_NAME in account realm-management; do

  # Extract client ID for this client.

  CLIENT_ID=$(curl -sk \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://onmshs/auth/admin/realms/opennms/clients | \
    jq ".[] | select(.clientId==\"$CLIENT_NAME\").id" | sed 's/"//g')

  printf "\nCLIENT_ID - $CLIENT_NAME: $CLIENT_ID\n"

  # Select the list of role names to be used for this client.

  if [[ $CLIENT_NAME == "account" ]]; then
    ROLE_NAMES="manage-account view-profile"
  else
    ROLE_NAMES="manage-users view-users query-users create-client"
  fi

  # Extract role IDs from roles under this client and put into the JSON fromat
  # required for the API.

  ROLES=$(curl -ks \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://onmshs/auth/admin/realms/opennms/clients/$CLIENT_ID/roles | jq)

  JSON=""

  for ROLE_NAME in $ROLE_NAMES;do
    ROLE_ID=$( echo $ROLES | \
      jq ".[] | select(.name==\"$ROLE_NAME\").id" | sed 's/"//g')

    JSON=$JSON', {"id":"'$ROLE_ID'", "name":"'$ROLE_NAME'"}'

  done

  # Add roles to the admin role.

  # Remove first ',' for JSON format.
  JSON=$( echo $JSON | sed 's/^,//g')
  echo "JSON: [ $JSON ]"
  curl -ks POST \
      -H "Authorization: Bearer $ACCESS_TOKEN" \
      -H "Content-Type: application/json" \
      -d  "[ $JSON ]"\
      https://onmshs/auth/admin/realms/opennms/users/$USER_ID/role-mappings/clients/$CLIENT_ID

done
