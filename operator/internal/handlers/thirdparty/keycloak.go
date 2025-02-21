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

package thirdparty

import (
	"github.com/OpenNMS-Cloud/opennms-operator/internal/handlers"
	"github.com/OpenNMS-Cloud/opennms-operator/internal/model/values"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
)

type KeycloakHandler struct {
	handlers.ServiceHandlerObject
}

func (h *KeycloakHandler) UpdateConfig(values values.TemplateValues) error {
	var initialAdminSecret corev1.Secret
	var realmConfigmap corev1.ConfigMap
	var deployment appsv1.Deployment
	var service corev1.Service

	h.AddToTemplates(handlers.Filepath("keycloak/keycloak-initial-cred-secret.yaml"), values, &initialAdminSecret)
	h.AddToTemplates(handlers.Filepath("keycloak/keycloak-realm-configmap-static.yaml"), values, &realmConfigmap)
	h.AddToTemplates(handlers.Filepath("keycloak/keycloak-deployment.yaml"), values, &deployment)
	h.AddToTemplates(handlers.Filepath("keycloak/keycloak-service.yaml"), values, &service)

	return h.LoadTemplates()
}
