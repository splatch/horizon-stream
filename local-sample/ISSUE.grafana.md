cd local-sample/
kind create cluster --config=config-kind.yaml

# Follow through the Install section (button on right): https://operatorhub.io/operator/keycloak-operator/candidate/keycloak-operator.v20.0.0-alpha.2
#curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.21.2/install.sh | bash -s v0.21.2
#kubectl create -f https://operatorhub.io/install/candidate/keycloak-operator.yaml

kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

cd new-keycloak/

# The following requires creating a couple of docker images.
./run.sh

rm .terraform.lock.hcl
rm terraform.tfstate
rm -r .terraform
terraform init && terraform apply -auto-approve

# Cred: admin:admin
https://localhost/auth
# IMPORTANT: Add email to admin user to get grafana to login correctly.

# Cred: admin:admin
https://localhost/grafana
