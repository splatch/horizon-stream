#!env bash

ARG_1_1="cluster"
ARG_1_2="image"
ARG_1_3="kill"
ARG_1_4="help"
ARG_2_1="create"
ARG_2_2="apply"
ARG_2_3="build"
ARG_2_4="ingress"
ARG_2_5="push"

BOOL_IMAGE_BUILD=0
BOOL_IMAGE_PUSH=0

PROPERTY_CLUSTER_NAME="horizon-stream"

# IMPORTANT - When adding a new test, make sure that everywhere $3 is used
# below is consistent in your test.

if [ "$1" = "$ARG_1_1" ] && [ "$2" = "$ARG_2_1" ]; then

printf "\n================================================================================\nCREATING & CONNECTING TO CLUSTER\n================================================================================\n\n"

# There seems to be issues if this is already populated.
export KUBECONFIG=''

k3d cluster create $PROPERTY_CLUSTER_NAME \
  --api-port 6550 \
  --servers 1 \
  --agents 2 \
  --registry-create registry-hs:49635 \
  --image rancher/k3s:v1.20.10-k3s1 \
  --port 443:443@loadbalancer \
  --wait \
  --verbose

printf "\nContainers started:\n"
docker ps
# The registry with port is there as well.

printf "\nCluster:\n"
k3d cluster list

# Set connection
k3d kubeconfig get horizon-stream > ~/.kube/horizon-stream
export KUBECONFIG=~/.kube/horizon-stream:~/.kube/config

printf "\nRun this to get access to cluster: export KUBECONFIG=~/.kube/horizon-stream:~/.kube/config\n\n"
printf "\nShutdown: \n  $ k3d cluster list\n  $ k3d cluster <cluster_name>\n\n"
LOC=$(pwd)
printf "\nLoc: %s\n\n" $LOC

printf "\n================================================================================\nINSTALLING INGRESS CONTROLLER\n================================================================================\n\n"

# Install ingress
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.47.0/deploy/static/provider/baremetal/deploy.yaml

kubectl -n ingress-nginx get pods

fi

if [ "$1" = "$ARG_1_1" ] && [ "$2" = "$ARG_2_2" ]; then

printf "\n================================================================================\nAPPLY DEPLOYMENT - $3\n================================================================================\n\n"

export KUBECONFIG=~/.kube/horizon-stream:~/.kube/config

cd $3
DATE_EPOCH=$(date +%s)
mvn clean install k8s:resource k8s:apply "-DversionSuffix=DATE_EPOCH"
#mvn clean install k8s:build k8s:resource k8s:undeploy "-DversionSuffix=DATE_EPOCH"
cd ..

# When creating this cluster the image needs to be built and pushed.
BOOL_IMAGE_BUILD=1
BOOL_IMAGE_PUSH=1

fi

if [ "$1" = "$ARG_1_2" ] && [ "$2" = "$ARG_2_3" ] || [ 1 = "$BOOL_IMAGE_BUILD" ]; then

printf "\n================================================================================\nBUILD IMAGE - $3\n================================================================================\n\n"

# Remove old entries.
docker rmi $(docker images "localhost:49635/$3" -a -q)

# Run the following for an example, webapp:
cd $3

DATE_EPOCH=$(date +%s)
mvn clean install k8s:build "-DversionSuffix=$DATE_EPOCH"

# Store here for other commands that are called. They will all work from the
# same version until a new build.
echo $DATE_EPOCH > "version.tmp"

# Test build in docker.
docker run --name $3 localhost:49635/$3:$DATE_EPOCH

printf "removed container: "; docker container rm $3

cd ../

fi

if [ "$1" = "$ARG_1_2" ] && [ "$2" = "$ARG_2_5" ] || [ 1 = "$BOOL_IMAGE_PUSH" ]; then

printf "\n================================================================================\nPUSHING & IMPORTING CREATED IMAGE INTO CLUSTER - $3\n================================================================================\n\n"

export KUBECONFIG=~/.kube/horizon-stream:~/.kube/config

cd $3

DATE_EPOCH=$(cat version.tmp)
docker push localhost:49635/$3:$DATE_EPOCH

# This set image triggers redeployment.
kubectl set image deployment/test-helloworld test-helloworld=registry-hs:49635/test-helloworld:$DATE_EPOCH

kubectl get all

# This does not always work correctly, multiple pods running.
kubectl logs deployment.apps/$3

cd ../

fi

if [ "$1" = "$ARG_1_1" ] && [ "$2" = "$ARG_2_4" ]; then

printf "\n================================================================================\nADDING INGRESS TO EXAMPLE\n================================================================================\n\n"

export KUBECONFIG=~/.kube/horizon-stream:~/.kube/config

cd $3

# This is only tested for test-webapp/.
kubectl -n ingress-nginx get pods

# Apply the following to the above webapp, put in a ingress-webapp.yaml file
kubectl apply -f ingress-webapp.yaml 
# Make sure that the port number in the file matches that of the service.

cd ..

fi 

if [ "$1" = "$ARG_1_3" ]; then

printf "\n================================================================================\nKILL PROCESS\n================================================================================\n\n"

k3d cluster delete $PROPERTY_CLUSTER_NAME
rm $3/version.tmp

# This cleans out configs back to Mac. Need to test this on Linux distros.
export KUBECONFIG=''

fi

if [ "$1" = "$ARG_1_4" ] || [ "$1" = "" ]; then

# ARG_1_1="cluster"
# ARG_1_2="image"
# ARG_1_2="kill"
# ARG_1_2="help"
# ARG_2_1="create"
# ARG_2_2="apply"
# ARG_2_3="build"
# ARG_2_4="ingress"
# ARG_2_5="push"

printf "Options: \n\n" 
printf "1. $ ./hs.sh %s %s\n" "$ARG_1_1" "$ARG_2_1" 
printf "2. $ ./hs.sh %s %s test-helloworld\n" "$ARG_1_1" "$ARG_2_2" 
printf "  %s\n  %s\n" \
  "This calls options 3 and 4 below." \
  "Deploying a kubernetes deployment without an uploaded image will cause it to fail."
printf "3. $ ./hs.sh %s %s test-helloworld\n" "$ARG_1_2" "$ARG_2_3" 
printf "4. $ ./hs.sh %s %s test-helloworld\n" "$ARG_1_2" "$ARG_2_5" 
printf "5. $ ./hs.sh %s %s test-helloworld\n" "$ARG_1_1" "$ARG_2_4" 
printf "6. $ ./hs.sh %s\n" "$ARG_1_3"  
printf "7. $ ./hs.sh %s\n" "$ARG_1_4"  
printf "\n"

printf "The dir test-helloworld is a test project, it can be swapped out for any other test project in this dir that follows its format.\n\n"

exit 0

fi

printf "\n================================================================================\nFINISH\n================================================================================\n\n"
