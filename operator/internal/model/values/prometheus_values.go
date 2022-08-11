package values

type PrometheusValues struct {
	Enabled     bool                    `yaml:"Enabled"`
	Server      PrometheusServiceValues `yaml:"Server"`
	PushGateway PrometheusServiceValues `yaml:"PushGateway"`
}

type PrometheusServiceValues struct {
	ServiceName string `yaml:"ServiceName"`
	Image       string `yaml:"Image"`
}
