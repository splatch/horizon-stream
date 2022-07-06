#!/bin/bash

# This method removes the quotes and works.
RESULT=`curl -sk --data "username=admin&password=admin&grant_type=password&client_id=admin-cli" https://keycloak/auth/realms/master/protocol/openid-connect/token`
ACCESS_TOKEN=`echo $RESULT | sed 's/.*access_token":"//g' | sed 's/".*//g'`

USER_ID=$(curl -ks \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://keycloak/auth/admin/realms/opennms/users | jq '.[] | select(.username=="admin").id' | sed 's/"//g')

printf "\nUSER_ID: $USER_ID\n"

for CLIENT_NAME in account realm-management; do
  
  # Extract client ID for this client.

  CLIENT_ID=$(curl -sk \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    https://keycloak/auth/admin/realms/opennms/clients | \
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
    https://keycloak/auth/admin/realms/opennms/clients/$CLIENT_ID/roles | jq)

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
      https://keycloak/auth/admin/realms/opennms/users/$USER_ID/role-mappings/clients/$CLIENT_ID

done

