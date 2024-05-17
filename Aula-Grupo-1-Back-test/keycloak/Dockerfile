FROM quay.io/keycloak/keycloak:24.0.3

COPY realm-export.json /opt/keycloak/data/import/realm-export.json
COPY classroom-theme /opt/keycloak/themes/classroom-theme

EXPOSE 8080

ENTRYPOINT ["/opt/keycloak/bin/kc.sh","start-dev", "--import-realm"]