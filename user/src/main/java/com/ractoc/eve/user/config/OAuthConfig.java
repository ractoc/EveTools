package com.ractoc.eve.user.config;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;

@Configuration
public class OAuthConfig {

    @Value("${sso.client-id}")
    private String clientId;
    @Value("${sso.client-secret}")
    private String clientSecret;

    @Bean
    public Client initializeClient() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        final ContextResolver<MoxyJsonConfig> jsonConfigResolver = moxyJsonConfig.resolver();
        Feature basicAuth = HttpAuthenticationFeature.basic(clientId, clientSecret);
        return ClientBuilder.newBuilder()
                .register(basicAuth)
                .register(jsonConfigResolver)
                .build();
    }
}
