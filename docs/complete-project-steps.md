# Spring Authorization Server Demo: Complete Project Steps

This file is the working roadmap for the whole project.
It explains what exists now, how to run it, how the profile and security switch work, and what to build next.

## 1. Current Goal

Turn this demo into a reusable Spring Boot security base that can be switched into different security styles depending on the app:

- `none`
- `basic`
- `form-session`
- `jwt-resource-server`
- `authorization-server`

The project is still one application today, but it is being shaped toward a reusable security service.

## 2. What Exists Right Now

Current building blocks:

- Spring Boot application entry point
- Spring Authorization Server configuration
- A security switch using `app-security.mode`
- A `NoneSecurityConfig`
- A `BasicSecurityConfig`
- Separate config classes for authorization server, token handling, persistence, and password encoding
- `AppSecurityProperties` and `SecurityMode`
- Flyway database migration support
- `dev`, `test`, `local`, and `prod` profiles

## 3. Project Profiles

Use profiles for environment, not for security mode.

### `local`

Default profile for running the app on your machine without MySQL.

- H2 database
- good for quick startup
- safe for learning and debugging

### `dev`

Explicit MySQL development profile.

- MySQL database
- use real local database credentials
- only use this when MySQL is available and configured

### `test`

Profile for tests.

- H2 database
- isolated and repeatable
- no dependency on your local MySQL setup

### `prod`

Strict production profile.

- externalized secrets
- no default credentials
- no local fallbacks

## 4. Security Switch

The main app security switch is:

```yaml
app-security:
  enabled: true
  mode: authorization-server
```

That switch controls which security configuration beans load.

### Modes

- `none`: open app, no login required
- `basic`: HTTP Basic auth
- `form-session`: browser login with session
- `jwt-resource-server`: API validates bearer JWTs
- `authorization-server`: this app issues OAuth2/OIDC tokens

## 5. How The Switch Works

Each config class is guarded with `@ConditionalOnProperty(prefix = "app-security", name = "mode", havingValue = "...")`.

That means:

- only one mode should be active at a time
- the default mode comes from `application.yaml`
- tests can override the mode locally with `@SpringBootTest(properties = "...")`

## 6. Current Config Classes

These classes have clear responsibilities now:

- `AuthorizationServerSecurityConfig`
- `AuthorizationServerPersistenceConfig`
- `TokenSecurityConfig`
- `PasswordSecurityConfig`
- `DefaultWebSecurityConfig`
- `NoneSecurityConfig`
- `BasicSecurityConfig`
- `SecurityModeLogger`

## 7. Configuration Files

### `application.yaml`

Shared defaults for every run.

Current job:

- set default profile to `local`
- set default security mode to `authorization-server`

### `application-local.yaml`

Default local H2 setup.

Current job:

- allow the app to start without MySQL
- support easy learning runs

### `application-dev.yaml`

Explicit MySQL dev setup.

Current job:

- MySQL connection only
- no fake defaults for production-like values

### `application-test.yaml`

Test-only H2 setup.

Current job:

- fast and isolated tests

### `application-prod.yaml`

Production externalized config.

Current job:

- no local assumptions
- no hardcoded credentials

## 8. How To Run

### Run tests

```bash
sh mvnw test
```

### Run locally with defaults

```bash
sh mvnw spring-boot:run
```

This uses the `local` profile by default.

### Run with none mode

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local --app-security.mode=none"
```

### Run with basic mode

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local --app-security.mode=basic"
```

### Run explicit dev mode

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev --app-security.mode=authorization-server"
```

## 9. Test Strategy

Keep three kinds of tests.

### A. Property binding tests

These prove the mode binds correctly.

Examples:

- default `authorization-server`
- `none`
- `basic`

### B. Bean loading tests

These prove the right config classes are active for each mode.

Examples:

- `NoneSecurityConfig` loads when mode is `none`
- `BasicSecurityConfig` loads when mode is `basic`
- authorization-server config does not load in those modes

### C. HTTP behavior tests

These prove the security actually behaves correctly.

Examples:

- `none` allows unauthenticated access to `/actuator/health`
- `basic` returns `401` without credentials

## 10. Recommended Build Order

Follow this order when adding more features.

1. Keep current modes passing
2. Add `BasicSecurityConfig` behavior tests
3. Add HTTP tests for `none` and `basic`
4. Add `form-session` mode
5. Add `jwt-resource-server` mode
6. Add common features like CORS, CSRF, headers, audit, and rate limiting
7. Add token claim customization and authority mapping
8. Improve authorization-server persistence and key handling
9. Extract reusable starter modules

## 11. What Still Needs Improvement

The big next steps are:

- `basic` should eventually have a proper HTTP test
- `none` should eventually be proved with a real endpoint test
- `jwt-resource-server` is the most useful next real mode
- authorization-server should use durable signing keys in production
- the custom password provider may later be replaced with a cleaner Spring-provided setup

## 12. Long-Term Module Shape

When the project becomes reusable, split it into:

- `security-core`
- `security-autoconfigure`
- `security-spring-boot-starter`
- `authorization-server-app`
- sample apps for each mode

## 13. Quick Rules

- Use profiles for environment.
- Use `app-security.mode` for security behavior.
- Keep `application.yaml` as the safe default.
- Use H2 for tests and local startup when possible.
- Keep MySQL as explicit dev, not the default learning path.

## 14. Working Command Cheatsheet

```bash
sh mvnw test
```

```bash
sh mvnw spring-boot:run
```

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local --app-security.mode=none"
```

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local --app-security.mode=basic"
```

```bash
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev --app-security.mode=authorization-server"
```

## 15. Best Next Step

Add HTTP tests for the active modes:

- `none` should permit access
- `basic` should challenge with `401`
- `authorization-server` should keep its current behavior

## 16. Detailed Goal Path

This is the full path from the current demo to the reusable security service.

### Phase A. Stabilize The Current App

Goal: keep the authorization server working while the code gets cleaner.

Steps:

1. Keep `sh mvnw test` green.
2. Keep `application.yaml` pointing to the safe default: `local` profile and `authorization-server` mode.
3. Keep `local` on H2 so the app can boot without MySQL.
4. Keep `dev` as the explicit MySQL profile.
5. Keep `test` isolated on H2.
6. Keep `prod` strict and fully externalized.

Done when:

- the app starts locally
- tests pass
- no profile is overloaded with security behavior

### Phase B. Prove The Switch

Goal: prove `app-security.mode` really changes behavior.

Steps:

1. Keep `AppSecurityProperties` bound from YAML.
2. Keep `SecurityMode` as the allowed mode list.
3. Keep `NoneSecurityConfig`, `BasicSecurityConfig`, and authorization-server configs conditional.
4. Add or keep bean-loading tests for `none`, `basic`, and `authorization-server`.
5. Add startup logging with `SecurityModeLogger`.

Done when:

- startup logs show the active mode
- bean-loading tests pass for every mode
- the switch value is obvious in the console

### Phase C. Prove HTTP Behavior

Goal: prove each mode changes real endpoint behavior.

Steps:

1. Add a test for `none` that calls `/actuator/health` and expects `200`.
2. Add a test for `basic` that calls `/actuator/health` and expects `401`.
3. Keep the authorization-server behavior unchanged for now.
4. Keep `MockMvc` tests focused on real HTTP results, not just bean presence.

Done when:

- the request path behaves differently for each mode
- the tests reflect actual user-facing security, not just wiring

### Phase D. Add Session Web Mode

Goal: support normal browser apps.

Steps:

1. Create `FormSessionSecurityConfig`.
2. Enable form login.
3. Keep CSRF enabled.
4. Keep logout enabled.
5. Add a profile or test for browser-style behavior.

Done when:

- browser pages require login
- session-based flows work
- CSRF is correct for browser use

### Phase E. Add JWT Resource Server Mode

Goal: support most microservices.

Steps:

1. Create `JwtResourceServerSecurityConfig`.
2. Read issuer/JWK settings from `app-security`.
3. Validate bearer JWTs.
4. Map claims like `roles` to Spring authorities.
5. Return clear JSON `401` and `403` responses.
6. Add a test app or mock JWT-based tests.

Done when:

- APIs validate tokens
- role claims map correctly
- this mode can be reused in another service

### Phase F. Improve Authorization Server

Goal: make the auth server production-grade.

Steps:

1. Stop generating signing keys only in memory for real deployments.
2. Store keys in a durable source.
3. Keep JDBC repositories for clients, authorizations, and consents.
4. Add the full schema migrations for Spring Authorization Server tables.
5. Make client registration less hardcoded.
6. Keep issuer and redirect URIs strict.

Done when:

- key material survives restarts
- clients are not hardcoded in the config
- auth server data survives production restarts

### Phase G. Extract Reusable Starter

Goal: turn this app into something other projects can consume.

Steps:

1. Extract shared types into `security-core`.
2. Extract conditional config into `security-autoconfigure`.
3. Package the starter as `security-spring-boot-starter`.
4. Keep this repo as the runnable sample app.
5. Add sample consumer apps for `basic`, `none`, `jwt-resource-server`, and `authorization-server`.

Done when:

- another Spring Boot app can add one dependency and use your security switch
- the reusable modules are not tied to this one sample app

## 17. Practical Milestones

If you want the journey in smaller chunks, use this order:

1. Make the current tests pass.
2. Add HTTP tests for `none`.
3. Add HTTP tests for `basic`.
4. Add `form-session`.
5. Add `jwt-resource-server`.
6. Improve auth-server keys and client persistence.
7. Extract the starter modules.

## 18. What Not To Do Yet

Avoid these for now:

- don’t extract modules before the modes work
- don’t add SAML, LDAP, or mTLS before `basic` and `jwt-resource-server`
- don’t replace the whole auth server structure before the switch model is stable
- don’t mix environment profiles and security modes

## 19. Decision Rules

When you wonder what to build next, use this rule:

- if it helps the app start, it belongs in profile config
- if it changes security behavior, it belongs in `app-security.mode`
- if it helps other apps reuse the code, it belongs in a shared module later

## 20. Current Working Default

Use these as the project defaults while learning:

- `local` profile for normal runs
- `test` profile for tests
- `dev` profile only when you want MySQL
- `app-security.mode=authorization-server` as the default security mode

