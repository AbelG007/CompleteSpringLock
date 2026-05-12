package com.authorizationServer.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@EnableConfigurationProperties(ClientAppProperties.class)
public class ClientOAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientAppProperties clientAppProperties) {
        ClientAppProperties.Registration registration = clientAppProperties.getRegistration();
        ClientAppProperties.AuthorizationServer provider = clientAppProperties.getAuthorizationServer();

        ClientRegistration.Builder clientRegistrationBuilder = ClientRegistration.withRegistrationId(registration.getRegistrationId())
                .clientId(registration.getClientId())
                .clientSecret(registration.getClientSecret())
                .clientAuthenticationMethod(toClientAuthenticationMethod(registration.getClientAuthenticationMethod()))
                .authorizationGrantType(toAuthorizationGrantType(registration.getAuthorizationGrantType()))
                .redirectUri(registration.getRedirectUri())
                .authorizationUri(provider.getAuthorizationUri())
                .tokenUri(provider.getTokenUri())
                .jwkSetUri(provider.getJwkSetUri())
                .userInfoUri(provider.getUserInfoUri())
                .userNameAttributeName(provider.getUserNameAttribute())
                .clientName(registration.getRegistrationId());

        clientRegistrationBuilder.scope(registration.getScopes().toArray(new String[0]));

        ClientRegistration clientRegistration = clientRegistrationBuilder.build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    private ClientAuthenticationMethod toClientAuthenticationMethod(String value) {
        if ("client_secret_basic".equals(value)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }
        if ("client_secret_post".equals(value)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        }
        if ("none".equals(value)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(value);
    }

    private AuthorizationGrantType toAuthorizationGrantType(String value) {
        if ("authorization_code".equals(value)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }
        if ("client_credentials".equals(value)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        }
        if ("refresh_token".equals(value)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(value);
    }
}
