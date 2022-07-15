# Local Development with Skaffold

## Prerequisites
* JDK 11 (https://www.oracle.com/java/technologies/downloads/#java11)
* Maven - Build tool for Java (https://maven.apache.org/download.cgi)
* Docker - Containerization (https://docs.docker.com/get-docker/)
* Kind - Spin up a local Kubernetes cluster (https://kind.sigs.k8s.io/docs/user/quick-start/#installing-with-a-package-manager)
* Skaffold - Dev tool for developing locally with Kubernetes (https://skaffold.dev/docs/install/)

This will be used for local devs, CI-CD, and users who want to deploy an on-prem solution.

Types of development & testing:
* Unit development & test - maven (current setup) - each Jar file.
   * More frequent development.
   * Microservice architecture allows us to develop and test components in isolation making it easier to manage each component.
   * Use docker or other non-k8s setup. Quickest and easiest for developers.
* Build time integration for multiple classes is another layer of integration … multiple layers of integration. We could use mocks for those lower layers of integration.
   * Integration of jars and classes into the complete software solution.
* External System Integration development & test - Uses Kubernetes with Operator deploying the env using Maven.
   * Development
      * Opennms-horizon-stream/dev/README.md will have a section that instructs users to setup kind and deploy operator and then deploy CRD for horizon stream version being developed locally. Much simpler with manual steps.
      * Does not happen as often as unit development on microservices, so steps can happen manually.
      * Could look at Skaffold at some point for this if more frequent development is required.
   * Test
      * Need the ability to deploy a cluster with operator on demand.
      * This will be done locally but will also be used for CI-CD pipeline.
      * Will use non-maven tool for integration testing at this level.

## Instructions
1. Start from the project's root directory.
2. ``` kind create cluster```
   * There are options to create multiple clusters with different names and switch between them. 
3. Confirm connection to cluster:
   * ``` kubectl config get-contexts```
   * ``` kubectl get all```

   **NOTE**: if you get the error `The connection to the server localhost:8080 was refused - did you specify the right host or port?`, run the following:
   * `kubectl config use-context kind-kind`
4. Install the Keycloak Operator
    ```shell
    $ kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
    $ kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
    $ kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/18.0.0/kubernetes/kubernetes.yml
    # Verify (should get keycloaks and keycloakrealmiiimports)
    $ kubectl api-resources | grep keycloak
    ```
5. Deploy the project into the cluster.
   1. Dev mode with file watching and port forwarding: `skaffold dev`
   2. Build and deploy once without enabling the dev loop: `skaffold run`
      * Forward ports automatically: `skaffold run --port-forward`
   3. Debug mode with automatic debug ports into containers: `skaffold debug`
      * Most of the dev loop is disabled in debug mode to prevent interfering with debug sessions. Reenable these features with `skaffold debug --auto-build --auto-sync --auto-deploy`
6. Wait for all services to come up.
7. Visit the front end in a web browser: http://localhost:3000/
8. Run the Keycloak scripts to test that the build was successful:
   ```shell
   cd tools
   ./KC.login -H localhost:28080 -u user001 -p passw0rd -R opennms
   ./events.list -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
   ./events.publish -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
   ./events.list -H localhost:18181 -t "$(< data/ACCESS_TOKEN.txt)"
   ```
   You should see log output with event JSON.
9. Manually configure keycloak to work with localhost login.
   1. Add realm `opennms`, if one does not exist from the scripts above. 
   2. Add client `horizon-stream`.
      * On the client page, set `Standard Flow Enabled` to `OFF`.
      * On the client page, add `*` to `Web Origins` to prevent CORS issues.
   3. Add a user
      * On the user credentials tab, add a password.
      * On the user details tab, deselect the `Update Password` chip in the `Required User Actions` field.
      

10. The mail serve web UI can be accessed at http://localhost:8025
Pruning docker images from process:
* Removes based on image-name:tag
```
for i in $(docker images | grep skaffold | awk '{print $1":"$2}'); do docker rmi $i; done; docker images
```
* Removes based on image id
```
for i in $(docker images | grep skaffold | awk '{print $3}'); do docker rmi $i; done; docker images
```
### Note 
**The key-value pair of service metadata.labels need to math the value of deployment spec.selector.matchLabels and the value of template.metadata.labels** otherwise the cluster network can't connect to correct pod hostname.
For example: don't use ```run: mail-server``` in one place and ```app: mail-server``` in other places

