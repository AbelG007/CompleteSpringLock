# Reusable Spring Security Module/Profile Design

## Current Project Shape

This repository is currently a single runnable Spring Boot authorization server app.

What it already has:

- Spring Boot app entry point: `AuthorizationServerApplication`
- Spring Authorization Server filter chain with OIDC enabled
- Default application filter chain with form login
- JDBC-backed registered clients, authorizations, and consents
- Database-backed users via `Customer`, `Authority`, and `UserDetailsServiceImpl`
- Password hashing via Spring Security delegating password encoder
- Compromised password checking via Have I Been Pwned
- Profile-specific database config for `local`, `dev`, `test`, and `prod`
- Flyway migration for users and authorities

What it should become:

- A reusable security starter that any Spring Boot app can add as a dependency
- A runnable authorization-server application for projects that need their own identity provider
- A set of property-driven modes and features, so each consuming app can pick only the security pieces it needs

## Target Maven Modules

The clean long-term shape is a parent Maven project with focused modules:

| Module | Packaging | Responsibility |
|---|---:|---|
| `security-bom` | `pom` | Dependency versions for all security modules |
| `security-core` | `jar` | Shared properties, enums, interfaces, role/claim mapping, common matchers |
| `security-spring-boot-starter` | `jar` | Main reusable starter consumed by other apps |
| `security-autoconfigure` | `jar` | Conditional Spring Boot auto-configuration classes |
| `security-authorization-server` | `jar` or app | Current repo logic: OAuth2/OIDC authorization server |
| `security-resource-server` | `jar` | JWT and opaque-token API protection |
| `security-session-web` | `jar` | Form login, session security, logout, remember-me |
| `security-api-key` | `jar` | API key authentication filter and key resolver SPI |
| `security-enterprise-sso` | `jar` | SAML2, LDAP, and OIDC login integrations |
| `security-hardening` | `jar` | CORS, CSRF, headers, HSTS, frame options, content security policy |
| `security-observability` | `jar` | Audit events, metrics, structured security logs |
| `security-samples` | `pom` | Example apps that consume the starter in different modes |

Short-term, you can keep one repo and first extract only:

1. `security-core`
2. `security-autoconfigure`
3. `security-spring-boot-starter`
4. `authorization-server-app`

## Configuration Model

Use one main property namespace:

```yaml
app-security:
  enabled: true
  mode: authorization-server
  features:
    cors: true
    csrf: auto
    headers: true
    method-security: true
    audit: true
    rate-limit: false
  public-paths:
    - /actuator/health
    - /actuator/info
  authority:
    role-prefix: ROLE_
    claim-name: roles
```

`mode` chooses the main authentication architecture. `features` are add-ons that can be combined with most modes.

## Main Modes

| Mode | Consuming App Gets | Best For |
|---|---|---|
| `none` | Security disabled except optional headers | Local experiments, public-only demos |
| `basic` | HTTP Basic auth | Internal tools, quick admin APIs |
| `form-session` | Login page, server session, logout | MVC/server-rendered apps |
| `jwt-resource-server` | Bearer JWT validation | Stateless REST APIs |
| `opaque-resource-server` | Bearer opaque token introspection | APIs using centralized token introspection |
| `api-key` | API key filter | Partner APIs, simple machine access |
| `oidc-login` | Login with external OIDC provider | Web apps using Google, Keycloak, Auth0, Okta |
| `oauth2-client` | OAuth2 client manager | Apps calling third-party protected APIs |
| `authorization-server` | OAuth2/OIDC token issuer | Your own identity provider |
| `saml2-login` | SAML2 relying party login | Enterprise SSO |
| `ldap-login` | LDAP/AD username-password login | Internal company apps |
| `mtls` | Certificate-based client authentication | Service-to-service security |
| `webauthn` | Passkey/passwordless login | High-security user login |

## Add-On Features

| Feature | Purpose |
|---|---|
| `cors` | Central CORS policy |
| `csrf` | CSRF strategy: `enabled`, `disabled`, or `auto` |
| `headers` | Security headers: HSTS, content type options, frame options, CSP |
| `method-security` | Enables `@PreAuthorize`, `@PostAuthorize`, and service-level rules |
| `rbac` | Role-based authorization conventions |
| `abac` | Attribute-based authorization hooks |
| `multi-tenant` | Tenant resolver and tenant-aware authorization |
| `audit` | Authentication and authorization event logging |
| `rate-limit` | Brute force and API abuse protection |
| `password-policy` | Password encoder, compromised password checks, strength rules |
| `token-claims` | Shared JWT claim customization |
| `key-rotation` | JWK loading, publishing, and rotation |
| `exception-json` | Consistent API error responses for 401/403 |

## Preset Profiles

Profiles are named recipes over `mode` plus `features`.

| Profile | Equivalent Setup |
|---|---|
| `local-open` | `mode: none`, actuator health public |
| `internal-basic-api` | `mode: basic`, headers, audit |
| `mvc-session-app` | `mode: form-session`, csrf, headers, audit |
| `public-rest-api` | `mode: jwt-resource-server`, cors, headers, exception-json |
| `enterprise-api` | `mode: opaque-resource-server`, introspection, audit, rate-limit |
| `third-party-client` | `mode: oauth2-client`, token relay, audit |
| `oidc-web-app` | `mode: oidc-login`, session, csrf, headers |
| `identity-provider` | `mode: authorization-server`, OIDC, JWK, JDBC persistence |
| `enterprise-sso-web` | `mode: saml2-login`, session, csrf, audit |
| `service-mesh-api` | `mode: mtls`, headers, audit |

Example:

```yaml
app-security:
  profile: public-rest-api
  resource-server:
    jwt:
      issuer-uri: http://127.0.0.1:9000
      audience: orders-api
```

## Current Classes To Extract

| Current Class/File | Target Location | Change Needed |
|---|---|---|
| `ProjectSecurityConfig` | Split across auto-config modules | Break into authorization server, login, token, password, key, and persistence configs |
| `UserDetailsServiceImpl` | Keep in app, expose SPI in starter | Starter should not force a `Customer` table |
| `PwdAuthenticationProvider` | Replace with configurable provider | Prefer Spring's `DaoAuthenticationProvider` unless custom behavior is required |
| `Customer`, `Authority`, `CustomerRepository` | Keep in authorization-server app sample | Reusable starter should depend on `UserDetailsService`, not JPA entities |
| `DatabaseMigrationConfig` | Keep in app/data module | Starter should not own app database migrations by default |
| `application-*.yaml` | Convert to examples | Keep profile examples for identity provider, API, MVC app |
| `V1__create_customer_and_authorities_tables.sql` | App migration | Add separate migrations for authorization server JDBC tables |

## Auto-Configuration Design

Each security capability should be a conditional auto-configuration class:

```text
AppSecurityAutoConfiguration
  AppSecurityProperties
  SecurityModeResolver

BasicSecurityAutoConfiguration
  active when app-security.mode=basic

FormSessionSecurityAutoConfiguration
  active when app-security.mode=form-session

JwtResourceServerAutoConfiguration
  active when app-security.mode=jwt-resource-server

AuthorizationServerAutoConfiguration
  active when app-security.mode=authorization-server

SecurityHardeningAutoConfiguration
  active when app-security.features.headers=true, cors=true, csrf=...

SecurityObservabilityAutoConfiguration
  active when app-security.features.audit=true
```

The consuming app should be able to override any bean by defining its own bean.

Important extension interfaces:

```java
public interface SecurityUserAuthorityMapper {
    Collection<? extends GrantedAuthority> map(UserDetails user);
}

public interface JwtClaimsContributor {
    void contribute(JwtEncodingContext context, Map<String, Object> claims);
}

public interface ApiKeyResolver {
    Optional<ApiKeyPrincipal> resolve(String rawApiKey);
}

public interface TenantResolver {
    Optional<String> resolveTenant(HttpServletRequest request, Authentication authentication);
}

public interface SecurityAuditPublisher {
    void publish(SecurityAuditEvent event);
}
```

## Authorization Server Profile

Your current project maps most closely to this:

```yaml
app-security:
  profile: identity-provider
  mode: authorization-server
  features:
    headers: true
    csrf: auto
    audit: true
    password-policy: true
    token-claims: true
    key-rotation: true
  authorization-server:
    issuer: ${AUTH_SERVER_ISSUER}
    oidc: true
    persistence: jdbc
    clients:
      source: jdbc
    tokens:
      access-token-format: self-contained
      access-token-ttl: 15m
      refresh-token-ttl: 8h
      include-roles-claim: true
      roles-claim-name: roles
    keys:
      source: jwks
      rotation-enabled: true
```

Production rules for this mode:

- Do not generate a new RSA key on every restart.
- Load signing keys from a keystore, database, Vault, or managed secret store.
- Use HTTPS in front of the authorization server.
- Require exact redirect URI matching for clients.
- Store registered clients, authorizations, and consents in durable storage.
- Add Flyway migrations for the Spring Authorization Server JDBC tables.
- Add an admin flow for client registration instead of hardcoding clients.

## Resource Server Profile

This is the most reusable mode for microservices:

```yaml
app-security:
  profile: public-rest-api
  mode: jwt-resource-server
  public-paths:
    - /actuator/health
    - /v3/api-docs/**
    - /swagger-ui/**
  resource-server:
    jwt:
      issuer-uri: ${AUTH_SERVER_ISSUER}
      jwk-set-uri: ${AUTH_SERVER_JWKS_URI:}
      audience: ${SERVICE_AUDIENCE}
      authorities-claim: roles
  features:
    cors: true
    csrf: false
    headers: true
    method-security: true
    audit: true
    exception-json: true
```

This should create one stateless `SecurityFilterChain`, configure JWT validation, map roles from claims, and return JSON `401/403` responses.

## Session Web Profile

```yaml
app-security:
  profile: mvc-session-app
  mode: form-session
  session:
    maximum-sessions: 1
    fixation-protection: migrate-session
  login:
    page: /login
    success-url: /
    failure-url: /login?error
  features:
    csrf: true
    headers: true
    audit: true
    password-policy: true
```

This should be separate from API token security because session apps and REST APIs have different defaults.

## Suggested Build Order

1. Fix the current authorization-server app so tests pass.
2. Split `ProjectSecurityConfig` into small configuration classes.
3. Introduce `AppSecurityProperties` and `SecurityMode`.
4. Implement `authorization-server` mode using the current behavior.
5. Add `jwt-resource-server` mode because it will be used by most apps.
6. Add `basic` and `form-session` modes for learning and internal apps.
7. Add common hardening, audit, and exception handling features.
8. Create one sample consuming app for each major profile.
9. Publish the starter locally and consume it from another Spring Boot app.
10. Add enterprise modules later: SAML2, LDAP, mTLS, WebAuthn.

## Immediate Findings From This Repo

- `ProjectSecurityConfig` now uses Spring's `org.springframework.jdbc.core.JdbcTemplate`; keep avoiding Flyway internal classes in application wiring.
- The authorization server JDBC repositories need matching schema migrations for registered clients, authorizations, and consents.
- RSA keys are generated in memory on startup, which is fine for learning but not reusable production security.
- The starter should not expose the `Customer` JPA model as a required contract.
- `authorizationServer` package naming should be normalized before extracting reusable modules, for example `com.example.security` or your own stable group id.

## Definition Of Done For The Starter

- A consuming app can add one starter dependency and configure `app-security.profile`.
- Every mode has a minimal passing sample.
- All public paths are explicit.
- Default production settings fail closed.
- All sensitive defaults can be externalized.
- Tests cover each `SecurityFilterChain` profile.
- Documentation shows one copy-paste YAML block per profile.
