# Requirements

Kind is installed.

Kubectl is installed.

Running on Mac or Linux.

```
sudo vi /etc/hosts

# Update with the following:
#127.0.0.1 localhosttest
#127.0.0.1 localhostkey
```

# Cleanup

```
# Delete cluster.
kind delete clusters kind

# Confirm all port-forwarding background processes are killed.
ps -axf | grep kubectl
```
