package com.ractoc.eve.crawler.config;

import com.ractoc.eve.universe_client.api.ConstellationResourceApi;
import com.ractoc.eve.universe_client.api.RegionResourceApi;
import com.ractoc.eve.universe_client.api.SolarsystemResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniverseClientConfig {

    @Bean
    public RegionResourceApi regionResourceApi() {
        return new RegionResourceApi();
    }

    @Bean
    public ConstellationResourceApi constellationResourceApi() {
        return new ConstellationResourceApi();
    }

    @Bean
    public SolarsystemResourceApi solarsystemResourceApi() {
        return new SolarsystemResourceApi();
    }

}
