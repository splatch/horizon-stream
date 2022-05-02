This dir is for building local dev envs.

Pre-requisites:
* Install Kind. Mac, there is brew install. 
* ```$ run.sh``` - Builds some components in dir.
* Make sure Docker is running. 
* Install Skaffold: https://skaffold.dev/docs/install/

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

# Skaffold Example

Process
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

# Process - Skaffold Build of HS Core and HS API Server

To start, we need to skaffold build each project to an image and deploy it to the local cluster (Kind).


