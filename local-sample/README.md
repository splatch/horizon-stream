# Requirements

Kind is installed.

Kubectl is installed.

Running on Mac or Linux.

```
sudo vi /etc/hosts
```
Add with the following to /etc/hosts:
```
127.0.0.1 localhostui
127.0.0.1 localhostkey
127.0.0.1 localhostapi
127.0.0.1 localhostcore
```

Run the following: ```cd local-sample```

Change the dns to the above dns entries in config-run to match the /etc/hosts file: ```vi config-run```

Run ```run.sh```. Takes a while.

Go to http://localhostui in the web browser. 

# Cleanup

```
# Delete cluster.
kind delete clusters kind

# Confirm all port-forwarding background processes are killed.
ps -axf | grep kubectl
```

# Testing Images Not Published But Built Locally

Rebuild the image:
```
cd ui/
docker build -t opennms/horizon-stream-ui:1.0.1 -f ./dev/Dockerfile .

kind load docker-image opennms/horizon-stream-ui:1.0.1

kubectl edit deployment.apps/my-horizon-stream-ui 
# Change: spec.template.spec.containers.image: opennms/horizon-stream-ui:latest -> spec.template.spec.containers.image: opennms/horizon-stream-ui:1.0.1
# Change: spec.template.spec.containers.imagePullPolicy: Always -> spec.template.spec.containers.imagePullPolicy: Never
```

