package com.ractoc.eve.crawler.config;

import com.ractoc.eve.jesi.api.UniverseApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsiConfig {

    @Bean
    public UniverseApi getUniverseApi() {
        return new UniverseApi();
    }
}