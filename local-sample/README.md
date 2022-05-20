# Requirements

Kind is installed.

Kubectl is installed.

Running on Mac or Linux.

```
sudo vi /etc/hosts

# Update with the following:
#127.0.0.1 localhostui
#127.0.0.1 localhostkey
#127.0.0.1 localhostapi
```

Change the following to the above dns entries:
```
vi ui/.env.development
##VITE_BASE_URL=http://localhost:9090
#VITE_BASE_URL=http://localhostevent
##VITE_KEYCLOAK_URL=http://localhost:28080
#VITE_KEYCLOAK_URL=http://localhostkey
```

TODO: Put the above as env variables to the docker image to be passed in through the k8s yaml file.

TODO: Put the following in rest-server/src/main/resources/application.yml as a env variable to the docker image to be passed in through the k8s yaml file. 
```
...
# keycloak
keycloak:
  realm: opennms
  auth-server-url: http://localhost:8080
  resource: horizon-stream
  public-client: true
  use-resource-role-mappings: true
  cors: true
...
```

Rebuild the image:
```
cd ui/
docker build -t opennms/horizon-stream-ui:1.0.1 -f ./dev/Dockerfile .

kind load docker-image opennms/horizon-stream-ui:1.0.1

kubectl edit deployment.apps/my-horizon-stream-ui 
# Change: spec.template.spec.containers.image: opennms/horizon-stream-ui:latest -> spec.template.spec.containers.image: opennms/horizon-stream-ui:1.0.1
# Change: spec.template.spec.containers.imagePullPolicy: Always -> spec.template.spec.containers.imagePullPolicy: Never
```

# Cleanup

```
# Delete cluster.
kind delete clusters kind

# Confirm all port-forwarding background processes are killed.
ps -axf | grep kubectl
```
