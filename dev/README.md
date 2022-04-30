This dir is for building local dev envs.

Pre-requisites:
* Install Kind. Mac, there is brew install. 
* ```$ run.sh``` - Builds some components in dir.
* Make sure Docker is running. 
* Install Skaffold: https://skaffold.dev/docs/install/

Process:
1. ``` kind create cluster``` 
    * There are options to create multiple clusters with different names and switch between them. 
2. Confirm connection to cluster:
    * ``` kubectl config get-contexts```
    * ``` kubectl get all```
2. ``` git clone https://github.com/GoogleContainerTools/skaffold.git tmp/skaffold```
3. ``` cd tmp/skaffold/examples/dev-journey-buildpacks/``` 
4. ``` skaffold dev --port-forward``` 
5. Go to http://localhost:8080/ in web browser. 
6. Open another tab in terminal to perform the following steps. 
7. ``` vi src/main/java/hello/HelloController.java```  
    * Make a change to the text. And wait until the skaffold dev tab has finished uploading change.
8. Go to http://localhost:8080/ in web browser. 
9. Ctrl-C to finish and cleanup.

Horizon Core:
1. ``` kind create cluster```
   * There are options to create multiple clusters with different names and switch between them. 
2. Confirm connection to cluster:
   * ``` kubectl config get-contexts```
   * ``` kubectl get all```
3. Build the horizon-stream-core docker image: ```mvn clean install -f platform -Pbuild-docker-images-enabled```
4. Load the local docker image into kind: ```kind load docker-image horizon-stream-core:local```
5. Apply the Kubernetes cluster: ```kubectl apply -f local-docker-compose/kubernetes.kafka.yaml```
6. Wait for all services to come up.
7. Forward ports through kubectl in two different terminals:
   ```shell
   kubectl port-forward service/horizon-stream 18181:8181
   ```
   ```shell
   kubectl port-forward service/keycloak 28080:8080 
   ```
8. Run the Keycloak scripts:
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
