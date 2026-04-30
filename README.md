# Spring Authorization Server Demo

This project is being upgraded into a standalone OAuth2/OIDC authorization server for reuse across microservices.

## Profiles

- `local`: default H2-backed local profile for running the app without MySQL
- `dev`: explicit MySQL-backed development profile
- `test`: isolated H2-backed test profile
- `prod`: strict production profile with required externalized database settings

The default profile is `local`.

## Required Environment Variables

### Common

- `AS_SERVER_PORT` optional, defaults to `9000`
- `AUTH_SERVER_ISSUER` optional in `dev`, required in `prod`

### Local

- No database variables required
- `AUTH_SERVER_ISSUER` optional, defaults to `http://127.0.0.1:9000`

### Dev

- `DATABASE_HOST` optional, defaults to `localhost`
- `DATABASE_PORT` optional, defaults to `3306`
- `DATABASE_NAME` optional, defaults to `eazybank`
- `DATABASE_USERNAME` optional, defaults to `root`
- `DATABASE_PASSWORD` optional, defaults to `root`

### Prod

- `DATABASE_HOST`
- `DATABASE_PORT`
- `DATABASE_NAME`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `AUTH_SERVER_ISSUER`

## Local Run

```bash
sh mvnw spring-boot:run
```

This now uses the `local` profile by default.

## Tests

Tests use the `test` profile and an in-memory H2 database, so they do not require a local MySQL instance.

```bash
sh mvnw test
```
