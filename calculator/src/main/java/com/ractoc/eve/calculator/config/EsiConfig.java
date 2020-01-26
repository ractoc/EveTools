package com.ractoc.eve.calculator.config;

import com.ractoc.eve.jesi.api.MarketApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsiConfig {

    @Bean
    public MarketApi getMarketApi() {
        return new MarketApi();
    }
}
