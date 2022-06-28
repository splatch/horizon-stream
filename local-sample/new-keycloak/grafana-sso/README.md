cd local-sample/grafana/

docker build -t grafana-test-sso .

kind load docker-image grafana-test-sso:latest
