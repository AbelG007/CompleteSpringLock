package com.authorizationServer.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "app-security.authorization-server")
public class AuthorizationServerClientProperties {

    private final List<Client> clients = new ArrayList<>();

    public List<Client> getClients() {
        return clients;
    }

    public static class Client {

        private String clientId;
        private String clientSecret;
        private String clientName;
        private List<String> authenticationMethods = new ArrayList<>();
        private List<String> authorizationGrantTypes = new ArrayList<>();
        private List<String> redirectUris = new ArrayList<>();
        private List<String> postLogoutRedirectUris = new ArrayList<>();
        private List<String> scopes = new ArrayList<>();
        private boolean requireProofKey;
        private boolean requireAuthorizationConsent;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public List<String> getAuthenticationMethods() {
            return authenticationMethods;
        }

        public void setAuthenticationMethods(List<String> authenticationMethods) {
            this.authenticationMethods = authenticationMethods;
        }

        public List<String> getAuthorizationGrantTypes() {
            return authorizationGrantTypes;
        }

        public void setAuthorizationGrantTypes(List<String> authorizationGrantTypes) {
            this.authorizationGrantTypes = authorizationGrantTypes;
        }

        public List<String> getRedirectUris() {
            return redirectUris;
        }

        public void setRedirectUris(List<String> redirectUris) {
            this.redirectUris = redirectUris;
        }

        public List<String> getPostLogoutRedirectUris() {
            return postLogoutRedirectUris;
        }

        public void setPostLogoutRedirectUris(List<String> postLogoutRedirectUris) {
            this.postLogoutRedirectUris = postLogoutRedirectUris;
        }

        public List<String> getScopes() {
            return scopes;
        }

        public void setScopes(List<String> scopes) {
            this.scopes = scopes;
        }

        public boolean isRequireProofKey() {
            return requireProofKey;
        }

        public void setRequireProofKey(boolean requireProofKey) {
            this.requireProofKey = requireProofKey;
        }

        public boolean isRequireAuthorizationConsent() {
            return requireAuthorizationConsent;
        }

        public void setRequireAuthorizationConsent(boolean requireAuthorizationConsent) {
            this.requireAuthorizationConsent = requireAuthorizationConsent;
        }

        public RegisteredClient toRegisteredClient(PasswordEncoder passwordEncoder) {
            RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientIdIssuedAt(Instant.now())
                    .clientName(clientName == null || clientName.isBlank() ? clientId : clientName)
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(requireProofKey)
                            .requireAuthorizationConsent(requireAuthorizationConsent)
                            .build())
                    .tokenSettings(TokenSettings.builder().build());

            if (clientSecret != null && !clientSecret.isBlank()) {
                builder.clientSecret(passwordEncoder.encode(clientSecret));
            }

            for (ClientAuthenticationMethod authenticationMethod : toClientAuthenticationMethods()) {
                builder.clientAuthenticationMethod(authenticationMethod);
            }

            for (AuthorizationGrantType authorizationGrantType : toAuthorizationGrantTypes()) {
                builder.authorizationGrantType(authorizationGrantType);
            }

            for (String redirectUri : redirectUris) {
                builder.redirectUri(redirectUri);
            }

            for (String postLogoutRedirectUri : postLogoutRedirectUris) {
                builder.postLogoutRedirectUri(postLogoutRedirectUri);
            }

            for (String scope : scopes) {
                builder.scope(scope);
            }

            return builder.build();
        }

        private Set<ClientAuthenticationMethod> toClientAuthenticationMethods() {
            Set<ClientAuthenticationMethod> methods = new LinkedHashSet<>();
            for (String method : authenticationMethods) {
                methods.add(toClientAuthenticationMethod(method));
            }
            return methods;
        }

        private Set<AuthorizationGrantType> toAuthorizationGrantTypes() {
            Set<AuthorizationGrantType> grantTypes = new LinkedHashSet<>();
            for (String grantType : authorizationGrantTypes) {
                grantTypes.add(toAuthorizationGrantType(grantType));
            }
            return grantTypes;
        }

        private ClientAuthenticationMethod toClientAuthenticationMethod(String value) {
            if (Objects.equals(value, "client_secret_basic")) {
                return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
            }
            if (Objects.equals(value, "client_secret_post")) {
                return ClientAuthenticationMethod.CLIENT_SECRET_POST;
            }
            if (Objects.equals(value, "none")) {
                return ClientAuthenticationMethod.NONE;
            }
            return new ClientAuthenticationMethod(value);
        }

        private AuthorizationGrantType toAuthorizationGrantType(String value) {
            if (Objects.equals(value, "authorization_code")) {
                return AuthorizationGrantType.AUTHORIZATION_CODE;
            }
            if (Objects.equals(value, "client_credentials")) {
                return AuthorizationGrantType.CLIENT_CREDENTIALS;
            }
            if (Objects.equals(value, "refresh_token")) {
                return AuthorizationGrantType.REFRESH_TOKEN;
            }
            return new AuthorizationGrantType(value);
        }
    }
}
