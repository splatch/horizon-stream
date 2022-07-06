
To get the /ui path on the ui ingress, update the following:
```
Ingress
        pathType: ImplementationSpecific
        no annotations

src/router/index.ts
  const router = createRouter({
    history: createWebHistory('/ui'),
    routes: [
      {
        path: '/',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: '/appliances',
        name: 'Appliances',
        component: Appliances
      },
      {
        path: '/:pathMatch(.*)*', // catch other paths and redirect
        redirect: '/'
      }
    ]
  })

Entrypoint.sh
  yarn run dev --host --base=/ui

Rebuild docker image, upload image, and redeploy deployment: 
$ kind load docker-image opennms/horizon-stream-ui:0.0.13
$ kubectl edit deployment.apps/my-horizon-stream-ui
  Also check the following, make sure these are correctly changed:
        - name: DOMAIN_API
          value: https://localhostapi/
        - name: DOMAIN_KEYCLOAK
          value: https://onmshs/auth


Have to go to https://onms-hs/ui/, the trailing slash is important.
```

Places to change domain, need to get this as global var (make sure to change /etc/hosts):
* grafana-a-configmap.yaml:     
  * domain = onmshs 
  * auth_url =  https://onms-hs/auth/realms/opennms/protocol/openid-connect/auth
* grafana-e-ingress.yaml:
  * host: onmshs
  * onms-hs # On the tls section.
* imports/test-opennms-realm.json
  * "frontendUrl": "https://onmshs/auth",
* kc-ingress.yaml:
  * host: onmshs
  * onms-hs # On the tls section.
* run.create-realm.sh
  * Every URL to https://onmshs/...
* kc-deployments.yaml
  * - name: KC_HOSTNAME
      value: onmshs

IMPORTANT:
* The user that logs into https://onms/ui/ needs to have admin role in keycloak to work with api endpoint.

Email:
1. Realm settings (admins will have to have their own SMTP server):
    * Host
    * Port
    * From: used the same as username below.
    * Enable StartTLS: ON
    * Enalbe Authentication: ON
    * Username: Same as From address above.
    * Password
    * Save
2. In opennms realm, clients->Account, confirm the baseurl is set to wherever the UI is.  
3. Add or update user
    * Go to the user's detials tab and add a valid email address, Save.
    * Go to the user's credentials and select an action from the 'Credential Reset' > 'Reset Actions' drop down. 
    * Select 'Send email'. 
    * Check inbox of email.

```
# IMPORTANT: When going to http://localhostui for the first time, it will fail
# because the self signed cert, you have to go to https://keycloak/auth and
# accept the risk, then it will be good.
# When all under same domain, should not matter.

cd grafana/
docker build -t grafana-test-sso .
cd ../keycloak-ui
docker build -t opennms/horizon-stream-keycloak:local -f ./Dockerfile .

cd local-sample/
./run.sh

# The following 2 statements are in 
kind load docker-image keycloak/keycloak:0.0.13
kind load docker-image grafana-test-sso:latest

#cd new-keycloak/
#./run.sh

# TODO: Need to update the domain name in the folloing configs, do it
# dynamically with sed for now.
run.create-realm.sh # Needs work.
# Login into https://keycloak/auth, then import the realm, user, and client
# from new-keycloak/imports. Need to select create in UI.

# Update entrypoint.sh with https on keycloak.
cd ../ui
vi dev/entrypoint.sh # Remove http or https from the $1 and $2, they are now passed in through config.
docker build -t opennms/horizon-stream-ui:local -f ./dev/Dockerfile .
kind load docker-image opennms/horizon-stream-ui:local
kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "image":"opennms/horizon-stream-ui:local"}]}}}}'
kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "imagePullPolicy":"Never"}]}}}}' 
kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_KEYCLOAK", "value":"https://keycloak/auth"}]}]}}}}'
kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_API", "value":"https://keycloak/api"}]}]}}}}'
################################################################################# 
# IMPORTANT: I changed the DOMAIN_API under the assumption that it is going to be under one domain, need to do that still.
################################################################################# 

cd ../platform
mvn -Prun-it clean install
kind load docker-image opennms/horizon-stream-core:local
kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "image":"opennms/horizon-stream-core:local"}]}}}}'
kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "imagePullPolicy":"Never"}]}}}}' 
kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "env":[{"name": "KEYCLOAK_BASE_URL", "value":"https://keycloak:8443/auth"}]}]}}}}'
kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-core", "env":[{"name": "KEYCLOAK_ADMIN_USERNAME", "value":"admin"}]}]}}}}'

cd ../rest-server
mvn clean install jib:dockerBuild -Dimage=opennms/horizon-stream-rest-server:local
kind load docker-image opennms/horizon-stream-rest-server:local
kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "image":"opennms/horizon-stream-rest-server:local"}]}}}}'
kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "imagePullPolicy":"Never"}]}}}}' 
kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "env":[{"name": "KEYCLOAK_AUTH_SERVER_URL", "value":"https://keycloak:8443/auth"}]}]}}}}'
kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-api", "env":[{"name": "HORIZON_STREAM_KEYCLOAK_ADMIN_USERNAME", "value":"admin"}]}]}}}}'

# Need to get this into shell REST API calls.
#cd ../local-sample/new-keycloak 
#rm .terraform.lock.hcl
#rm -r .terraform
#rm terraform.tfstate
#terraform init && terraform apply -auto-approve

# Did not work.
# I have updated the ingress of api with hostname as keycloak and path as /api
#kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_API", "value":"keycloak/api"}]}]}}}}'

# TODO - Get all under one domain.
```

# IMPORTANT: I need to login to https://keycloak/auth before
# http://localhostui, the login will not appear on http://localhostui until you
# login to https://keycloak/auth first.
# Also, add email to admin account for grafana. Cannot have empty email on email field for user.
# Need to add password to user001 or whatever user has been added to client horizon-stream in realm opennms.
