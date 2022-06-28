# docker build . -t keycloak/keycloak:latest
FROM quay.io/keycloak/keycloak:18.0.1 as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=token-exchange
ENV KC_DB=postgres

USER 0
RUN microdnf install tar # allows for Skaffold file sync

# Not permitted, this was to allow us to use 443 port on this image. Not good,
# but issue trying to connect to backend.
#RUN setcap 'cap_net_bind_service=+ep' /opt/keycloak/bin/kc.sh

# Install custom providers
RUN curl -sL https://github.com/aerogear/keycloak-metrics-spi/releases/download/2.5.3/keycloak-metrics-spi-2.5.3.jar -o /opt/keycloak/providers/keycloak-metrics-spi-2.5.3.jar
RUN /opt/keycloak/bin/kc.sh build

USER 1000

FROM quay.io/keycloak/keycloak:18.0.1
COPY --from=builder /opt/keycloak/ /opt/keycloak/
WORKDIR /opt/keycloak

COPY --chown=1000 themes themes
#COPY --chown=0 themes themes

# for demonstration purposes only, please make sure to use proper certificates in production instead
RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore
# change these values to point to a running postgres instance

#ENV KC_DB_URL=<DBURL>
#ENV KC_DB_USERNAME=<DBUSERNAME>
#ENV KC_DB_PASSWORD=<DBPASSWORD>
#ENV KC_HOSTNAME=localhost

#ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"] # Passed in through args on k8s yaml.
