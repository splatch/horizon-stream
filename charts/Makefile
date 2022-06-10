.PHONY: package

all: package

package:
	helm package onms-kafka -d packaged
	helm package onms-keycloak -d packaged
	helm repo index --url https://opennms.github.io/horizon-stream/charts/packaged --merge packaged/index.yaml ./packaged