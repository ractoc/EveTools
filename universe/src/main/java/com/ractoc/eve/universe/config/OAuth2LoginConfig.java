package com.ractoc.eve.universe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class OAuth2LoginConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("eve")
                .clientId("cf33fd256e884c81b634bf74ca21612a")
                .clientSecret("SEmd2uiGUpW0Flvxaqe1XiTV6M6Ii8GvFd4dyyZy")
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("http://localhost:8585/MBS")
                .scope("esi-assets.read_assets.v1")
                .authorizationUri("https://login.eveonline.com/v2/oauth/authorize/")
                .tokenUri("https://login.eveonline.com/v2/oauth/token")
                .clientName("Eve")
                .build();
    }
}
