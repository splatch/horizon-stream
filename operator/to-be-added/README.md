Add this to the Wiki.

# Requirements

Make sure the following are installed:
* Kind
* Kubectl
* Operator-sdk (https://sdk.operatorframework.io/docs/installation/)
* Helm3
* Docker

IMPORTANT: Run from repo root dir.
```
sudo vi /etc/hosts
```

Add with the following to /etc/hosts:
```
127.0.0.1 onmshs
```

# Init

Run the following from repo root dir:
```
cd operator/
bash ./scripts/deploy-horizon-stream.sh
```

Now add the changes from the following sections.

# UI

Run the following from the root dir of repo:
```
cd ui/
docker build -t opennms/horizon-stream-ui:local -f ./dev/Dockerfile .
kind load docker-image opennms/horizon-stream-ui:local
```

Update the deployment with this image and change the image pull policy to Never.

Create the keys and deploy internal-reverse-proxy.yaml:
```
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx-custom.key -out nginx-custom.crt -subj "/CN=nginx-custom/O=nginx-custom"
kubectl -n local-instance create secret tls test-nginx-custom-tls --key="nginx-custom.key" --cert="nginx-custom.crt"

kubectl apply -f ../operator/to-be-added/internal-reverse-proxy.yaml
# This contains a reverse proxy to take the HTTPS backend coming in through the
# UI and accepts SSL and then passes the user onto the opennms-ui service on
# http. I could not figure out how to get vite and yarn to work with SSL.
```

Update the opennms-ingress that has the '/' path to point to 'name: nginx-custom' and 'port.number: 443':
```
          - path: /
            pathType: Prefix
            backend:
              service:
                name: nginx-custom
                port:
                  number: 443
```

# Shared-lib

Run the following from the root dir of repo:
```
cd shared-lib/
mvn clean install
```

Required for platform and rest-server.

# Rest-server

Run the following from the root dir of repo:
```
cd ../rest-server/
mvn clean install jib:dockerBuild -Dimage=opennms/horizon-stream-rest-server:local
kind load docker-image opennms/horizon-stream-rest-server:local
```

Update the deployment with this image and change the image pull policy to Never.

For the keystore for the rest-server, create the keys as follows:
```
cd operator/to-be-added

# For in the cluster, the CN needs to be the service in front of the pod. # It is what the ingress will be forwarding to.
keytool -genkey -alias onms -keyalg RSA -keystore tmp/onms.keystore \
          -validity 3650 -storetype JKS \
          -dname "CN=opennms-rest-server, OU=Spring, O=Pivotal, L=Holualoa, ST=HI, C=US" \
          -keypass opennmspw -storepass opennmspw

kubectl -n local-instance create configmap rest-server.keystore \
  --from-file=onms.keystore

kubectl -n local-instance create configmap rest-server.application \
  --from-file=../../rest-server/src/main/resources/application.yml
```

The volume mounts have already been added to the rest-server deployment configs in the helm charts for the operator.

# Core

Run the following from the root dir of repo:
```
cd ../platform/
mvn -Prun-it -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 clean install
docker tag opennms-horizon-application-it-image:latest opennms/horizon-stream-core:local
kind load docker-image opennms/horizon-stream-core:local
```

Update the deployment with this image and change the image pull policy to Never.
