```
cd local-sample/
./run.sh

cd new-keycloak/
./run.sh

# Login into https://keycloak/auth, then import the realm, user, and client
# from new-keycloak/imports. Need to select create in UI.

# Update entrypoint.sh with https on keycloak.
vi dev/entrypoint.sh
docker build -t opennms/horizon-stream-ui:local -f ./dev/Dockerfile .
kind load docker-image opennms/horizon-stream-ui:local

kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "image":"opennms/horizon-stream-ui:local"}]}}}}'
kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "imagePullPolicy":"Never"}]}}}}' 
kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_KEYCLOAK", "value":"keycloak/auth"}]}]}}}}'

# Did not work.
# I have updated the ingress of api with hostname as keycloak and path as /api
#kubectl patch deployments my-horizon-stream-ui   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "DOMAIN_API", "value":"keycloak/api"}]}]}}}}'

# Do the following manually if they fail.

kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "KEYCLOAK_AUTH_SERVER_URL", "value":"https://keycloak:8443/auth"}]}]}}}}'
kubectl patch deployments my-horizon-stream-api   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "HORIZON_STREAM_KEYCLOAK_ADMIN_USERNAME", "value":"admin"}]}]}}}}'

kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "KEYCLOAK_BASE_URL", "value":"https://keycloak:8443/auth"}]}]}}}}'
kubectl patch deployments my-horizon-stream-core   -p '{"spec": {"template": {"spec":{"containers":[{"name": "horizon-stream-ui", "env":[{"name": "KEYCLOAK_ADMIN_USERNAME", "value":"admin"}]}]}}}}'
```

# IMPORTANT: I need to login to https://keycloak/auth before
# http://localhostui, the login will not appear on http://localhostui until you
# login to https://keycloak/auth first.
# Also, add email to admin account for grafana. Cannot have empty email on email field for user.
# Need to add password to user001 or whatever user has been added to client horizon-stream in realm opennms.
