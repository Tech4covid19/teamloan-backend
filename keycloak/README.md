# Keycloak on docker
For development (keycloak with h2) just run:
```bash
docker run -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -e KEYCLOAK_IMPORT=/tmp/realm-export.json -v ./realm-export.json:/tmp/realm-export.json jboss/keycloak

docker run -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -e KEYCLOAK_IMPORT=/tmp/realm-export.json -v /Users/alves/git-external/tech4covid/teamloan-backend/keycloak/realm-export.json:/tmp/realm-export.json --mount type=bind,source=/Users/alves/git-external/tech4covid/teamloan-backend/keycloak/themes/teamloan-theme,target=/opt/jboss/keycloak/themes/teamloan-theme jboss/keycloak
``
