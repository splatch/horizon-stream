package values

type PrometheusValues struct {
	ServerValues      PrometheusServiceValues `yaml:"Server"`
	PushGatewayValues PrometheusServiceValues `yaml:"PushGateway"`
}

type PrometheusServiceValues struct {
	ServiceName string `yaml:"ServiceName"`
	Image       string `yaml:"Image"`
}
