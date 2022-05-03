# Local Development with Skaffold

## Prerequisites
* JDK 11 (https://www.oracle.com/java/technologies/downloads/#java11)
* Maven - Build tool for Java (https://maven.apache.org/download.cgi)
* Docker - Containerization (https://docs.docker.com/get-docker/)
* Kind - Spin up a local Kubernetes cluster (https://kind.sigs.k8s.io/docs/user/quick-start/#installing-with-a-package-manager)
* Skaffold - Dev tool for developing locally with Kubernetes (https://skaffold.dev/docs/install/)

## Instructions
1. ``` kind create cluster```
   * There are options to create multiple clusters with different names and switch between them. 
2. Confirm connection to cluster:
   * ``` kubectl config get-contexts```
   * ``` kubectl get all```
3. Deploy the project into the cluster.
   1. Dev mode with file watching and port forwarding: `skaffold dev`
   2. Build and deploy once, forward ports to make services accessible:
      * `skaffold run`
      * `kubectl port-forward service/horizon-stream-core 18181:8181`
      * `kubectl port-forward service/keycloak 28080:8080`
      * `kubectl port-forward deployment/my-horizon-stream-ui 33000:3000`
4. Wait for all services to come up.
5. Visit the front end in a web browser: http://localhost:3000/
6. Run the Keycloak scripts to test that the build was successful:
   ```shell
   cd tools
   ./KC.login -H localhost:28080 -u keycloak-admin -p admin -R master
   ./KC.add-realm -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -R opennms
   ./KC.create-user -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -u user001 -p passw0rd -R opennms
   ./KC.get-user-by-username -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -u user001 -R opennms | tee data/user.out
   jq -r '.[] | .id' data/user.out  | tee data/user-id.txt
   ./KC.create-role -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -R opennms -r admin
   ./KC.get-realm-roles -H localhost:28080 -t "$(< data/ACCESS_TOKEN.txt)" -R opennms | tee data/role.out
   jq -r '.[] | select(.["name"] == "admin") | .id' data/role.out | tee data/role-id.txt
   ./KC.assign-user-role -H localhost:28080 -r admin -i "$(< data/role-id.txt)" -t "$(< data/ACCESS_TOKEN.txt)" -U "$(< data/user-id.txt)" -R opennms
   ./KC.login -H localhost:28080 -u user001 -p passw0rd -R opennms
   ./events.list -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
   ./events.publish -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
   ./events.list -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
   ```
   You should see log output with event JSON.
