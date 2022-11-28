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
	"github.com/OpenNMS/opennms-operator/internal/model/values"
	adminv1 "k8s.io/api/admissionregistration/v1"
	appsv1 "k8s.io/api/apps/v1"
	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	netv1 "k8s.io/api/networking/v1"
	rbacv1 "k8s.io/api/rbac/v1"
)

type IngressHandler struct {
	ServiceHandlerObject
}

func (h *IngressHandler) UpdateConfig(values values.TemplateValues) error {

	//INGRESS CONTROLLER CONFIGS
	var controllerServiceAccount corev1.ServiceAccount
	var controllerClusterRole rbacv1.ClusterRole
	var controllerClusterRoleBinding rbacv1.ClusterRoleBinding
	var controllerRole rbacv1.Role
	var controllerRoleBinding rbacv1.RoleBinding
	var controllerConfigMap corev1.ConfigMap
	var controllerService corev1.Service
	var controllerServiceAdmission corev1.Service
	var controllerIngressClass netv1.IngressClass
	var controllerDeployment appsv1.Deployment

	h.AddToTemplates(filepath("ingress/nginx-controller/controller-serviceaccount.yaml"), values, &controllerServiceAccount)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-clusterrole.yaml"), values, &controllerClusterRole)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-clusterrolebinding.yaml"), values, &controllerClusterRoleBinding)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-role.yaml"), values, &controllerRole)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-rolebinding.yaml"), values, &controllerRoleBinding)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-configmap.yaml"), values, &controllerConfigMap)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-service.yaml"), values, &controllerService)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-service-admission.yaml"), values, &controllerServiceAdmission)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-ingress-class.yaml"), values, &controllerIngressClass)
	h.AddToTemplates(filepath("ingress/nginx-controller/controller-deployment.yaml"), values, &controllerDeployment)

	//CUSTOM ERRORS CONFIGS
	//FIXME Is this even used?
	var customErrorsDeployment appsv1.Deployment
	var customErrorsService corev1.Service

	h.AddToTemplates(filepath("ingress/custom-errors/nginx-errors-deployment.yaml"), values, &customErrorsDeployment)
	h.AddToTemplates(filepath("ingress/custom-errors/nginx-errors-service.yaml"), values, &customErrorsService)

	//VALIDATING WEBHOOK CONFIGS
	var validatingWebhook adminv1.ValidatingWebhookConfiguration
	var webhookServiceAccount corev1.ServiceAccount
	var webhookRole rbacv1.Role
	var webhookRoleBinding rbacv1.RoleBinding
	var webhookClusterRole rbacv1.ClusterRole
	var webhookClusterRoleBinding rbacv1.ClusterRoleBinding

	h.AddToTemplates(filepath("ingress/validating-webhook/validating-webhook.yaml"), values, &validatingWebhook)
	h.AddToTemplates(filepath("ingress/validating-webhook/webhook-serviceaccount.yaml"), values, &webhookServiceAccount)
	h.AddToTemplates(filepath("ingress/validating-webhook/webhook-role.yaml"), values, &webhookRole)
	h.AddToTemplates(filepath("ingress/validating-webhook/webhook-rolebinding.yaml"), values, &webhookRoleBinding)
	h.AddToTemplates(filepath("ingress/validating-webhook/webhook-clusterrole.yaml"), values, &webhookClusterRole)
	h.AddToTemplates(filepath("ingress/validating-webhook/webhook-clusterrolebinding.yaml"), values, &webhookClusterRoleBinding)

	//JOBS CONFIGS
	var createSecret batchv1.Job
	var patchWebhook batchv1.Job

	h.AddToTemplates(filepath("ingress/jobs/job-createsecret.yaml"), values, &createSecret)
	h.AddToTemplates(filepath("ingress/jobs/job-patchwebhook.yaml"), values, &patchWebhook)

	//INGRESSES
	var opennmsIngress netv1.Ingress

	h.AddToTemplates(filepath("ingress/ingresses/opennms-ingress.yaml"), values, &opennmsIngress)

	return h.LoadTemplates()

}
