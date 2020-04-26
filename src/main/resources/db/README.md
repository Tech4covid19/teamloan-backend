# Database scripts
This projects uses flyway to run the database scripts. It does so by running flyway migrations every time the app is started according to the property:

```
quarkus.flyway.migrate-at-start=true
```

## Structure
Database scripts should be placed on the src/main/resources/db/migration and have the following name pattern:

```
V<artifactVersion>_<creationEpochInSeconds>__<description>.sql
```

If the script contains DML (only data queries like INSERT, UPDATE, MERGE, DELETE) the `<description>` part should end with `_data`. 
A script should never contain simultaneously DDL and DML because of transactional and organizational reasons.
