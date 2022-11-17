package handlers

import (
	"github.com/OpenNMS/opennms-operator/internal/model/values"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
)

type PrometheusHandler struct {
	ServiceHandlerObject
}

func (h *PrometheusHandler) UpdateConfig(values values.TemplateValues) error {
	if values.Values.Prometheus.Enabled == false {
		return nil
	}
	var prometheusConfigMap corev1.ConfigMap
	var prometheusService corev1.Service
	var prometheusDeployment appsv1.Deployment
	var pushGatewayService corev1.Service
	var pushGateWayDeployment appsv1.Deployment

	h.AddToTemplates(filepath("prometheus/prometheus-configmap.yaml"), values, &prometheusConfigMap)
	h.AddToTemplates(filepath("prometheus/prometheus-service.yaml"), values, &prometheusService)
	h.AddToTemplates(filepath("prometheus/prometheus-deployment.yaml"), values, &prometheusDeployment)
	h.AddToTemplates(filepath("prometheus/pushgateway-service.yaml"), values, &pushGatewayService)
	h.AddToTemplates(filepath("prometheus/pushgateway-deployment.yaml"), values, &pushGateWayDeployment)

	return h.LoadTemplates()
}
