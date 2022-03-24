This builds and tests java builds against kubernetes cluster that is deployed on k3s.

Requirements: 
* k3d
  * See https://github.com/k3d-io/k3d
  * $ brew install k3d
  * Tested:
    * k3d version v5.3.0
    * k3s version v1.22.6-k3s1 (default)
* mvn
  * Tested:
    * Apache Maven 3.8.4 (9b656c72d54e5bacbed989b64718c159fe39b537)
    * Java version: 11.0.14, vendor: BellSoft

IMPORTANT: If you have ~/.kube/config defined for other clusters, then hs.sh script will update the KUBECONFIG env var, ```export KUBECONFIG=~/.kube/horizon-stream:~/.kube/config```. As a result, if you use ```kubectl config use-context <contex_name>``` or Docker Desktop to switch contexts it doesn't always work properly or across all terminal sessions. Therefore, update ```export KUBECONFIG=""``` or ```export KUBECONFIG=~/.kube/config```.

Run the following:
```hs.sh help```

Tested for test-helloworld, but test-webapp not tested recently.

The following sample tests were taken from https://github.com/eclipse/jkube/tree/master/quickstarts/maven and then modified:
* test-helloworld/
* test-webapp/

Next steps:
1. Need to see how horizon stream docker-maven-plugin builds images. This may be the better choice for building images.
2. Need to make k8s yaml files for all those builds.
3. Integrate smoke, unit, and integration tests.
