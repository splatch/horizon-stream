Add this to the Wiki.

# Requirements

Make sure the following are installed:
* Kind
* Kubectl
* Operator-sdk (https://sdk.operatorframework.io/docs/installation/)
* Helm3
* Docker

ISSUE.solution:
* We had SSL between the ingress and backend because of keycloak, remove all this, we now have http for backend of keycloak. There is a lot to remove for this.

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
mvn -Prun-it -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 clean install -DskipTests
docker tag opennms-horizon-application-it-image:latest opennms/horizon-stream-core:local
kind load docker-image opennms/horizon-stream-core:local
```

Update the deployment with this image and change the image pull policy to Never.

Thange the URL in the following to the https://onms-keycloak:8443:
```
platform/docker-it/pom.xml:252:                  <alias>keycloak-host</alias>
platform/docker-it/src/test/resources/karaf/etc/org.opennms.core.rest.cfg:1:keycloak.base-url=http://keycloak-host:8080
core/rest/src/main/resources/OSGI-INF/blueprint/blueprint.xml:28:            <cm:property name="keycloak.base-url" value="$[env:KEYCLOAK_BASE_URL]"/>
```

One of the above, 'env:KEYCLOAK_BASE_URL' is in the core deployment. Add the other below. Changed that to 'http://onms-keycloak-http:8080/auth' in the deployment.
* The above is the new service for keycloak with http, need to update everywhere.

ISSUE: When I try to rebuild the container with the updated org.opennms.core.rest.cfg, it fails, it seems to be expecting the http://keycloak-host. Below I am trying to overwrite it in the pod.

Run the following, need to update this file with credentials and proper URL:
```
cd operator/to-be-added/

kubectl -n local-instance create configmap core.cfg \
  --from-file=org.opennms.core.rest.cfg
```

Volume mount to core:
```
spec.template.spec.volumes:
        - name: core-rest-cfg
          configMap:
            name: core.cfg
            defaultMode: 420

spec.template.spec.containers:
          volumeMounts:
            - name: core-rest-cfg
              mountPath: /opt/horizon-stream/etc/org.opennms.core.rest.cfg 
              subPath: org.opennms.core.rest.cfg
```

ISSUE: Unable to verify SSL from core to onms-keycloak service. Either, we need to remove SSL and use http or remove verification of ssl on core.
```
Failed to load URLs from https://onms-keycloak:8443/auth/realms/opennms/.well-known/openid-configuration
javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

```

Fixed, this fixes a lot, including HTTPS moving to HTTP between ingress and servers. In the keycloak CRD, add the following:
```
  serverConfiguration:
    - name: http-enabled
      value: 'true
```

Test, once the above has been added: 
```
kubectl apply -f keycloak-http-service-test.yaml
```

If works, then get all ingress backend traffic back to http.

New error in the opennms-core logs, I cannot figure out where opennms-core is getting that 'onmshs:443 [onmshs/127.0.0.1]' below:
```
02:21:41.926 ERROR [qtp826475652-459] Error when sending request to retrieve realm keys
org.keycloak.adapters.HttpClientAdapterException: IO error
	at org.keycloak.adapters.HttpAdapterUtils.sendJsonHttpRequest(HttpAdapterUtils.java:57)
	at org.keycloak.adapters.rotation.JWKPublicKeyLocator.sendRequest(JWKPublicKeyLocator.java:100)
	at org.keycloak.adapters.rotation.JWKPublicKeyLocator.getPublicKey(JWKPublicKeyLocator.java:63)
...
Caused by: org.apache.http.conn.HttpHostConnectException: Connect to onmshs:443 [onmshs/127.0.0.1] failed: Connection refused (Connection refused)
	at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:156)
	at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:376)
...
Caused by: java.net.ConnectException: Connection refused (Connection refused)
	at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.base/java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:399)
...
02:21:41.943 ERROR [qtp826475652-459] Didn't find publicKey for kid: 2hsCFCFNFfTe8ZYxxLgzqdWPz-Y4AwCBDqFFEZMtf3o
02:21:41.943 INFO  [qtp826475652-459] JWT authentication failure
org.keycloak.common.VerificationException: Didn't find publicKey for specified kid
...
```

