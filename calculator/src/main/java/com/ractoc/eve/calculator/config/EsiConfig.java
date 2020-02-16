package com.ractoc.eve.calculator.config;

import com.ractoc.eve.jesi.api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsiConfig {

    @Bean
    public MarketApi getMarketApi() {
        return new MarketApi();
    }

    @Bean
    public IndustryApi getIndustryApi() {
        return new IndustryApi();
    }

    @Bean
    public SkillsApi getSkillsApi() {
        return new SkillsApi();
    }

    @Bean
    public UniverseApi getUniverseApi() {
        return new UniverseApi();
    }

    @Bean
    public AssetsApi getAssetsApi() {
        return new AssetsApi();
    }
}
