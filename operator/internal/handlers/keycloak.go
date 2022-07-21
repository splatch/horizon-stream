/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package handlers

import (
	keycloakv2alpha1 "github.com/OpenNMS/opennms-operator/api/keycloak/v2alpha1"
	"github.com/OpenNMS/opennms-operator/internal/model/values"
	"github.com/OpenNMS/opennms-operator/internal/util/yaml"
	olmv1 "github.com/operator-framework/api/pkg/operators/v1"
	olmv1alpha1 "github.com/operator-framework/api/pkg/operators/v1alpha1"
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

type KeycloakHandler struct {
	ServiceHandlerObject
}

func (h *KeycloakHandler) ProvideConfig(values values.TemplateValues) []client.Object {
	var opGroup olmv1.OperatorGroup
	var opSub olmv1alpha1.Subscription
	var credSecret corev1.Secret
	var keycloak keycloakv2alpha1.Keycloak
	var realmImport keycloakv2alpha1.KeycloakRealmImport

	yaml.LoadYaml(filepath("keycloak/keycloak-operator-group.yaml"), values, &opGroup)
	yaml.LoadYaml(filepath("keycloak/keycloak-operator-sub.yaml"), values, &opSub)
	yaml.LoadYaml(filepath("keycloak/keycloak-cred-secret.yaml"), values, &credSecret)
	yaml.LoadYaml(filepath("keycloak/keycloak.yaml"), values, &keycloak)
	yaml.LoadYaml(filepath("keycloak/keycloak-realmimport.yaml"), values, &realmImport)

	h.Config = []client.Object{
		&opGroup,
		&opSub,
		&credSecret,
		&keycloak,
		&realmImport,
	}

	return h.Config
}
