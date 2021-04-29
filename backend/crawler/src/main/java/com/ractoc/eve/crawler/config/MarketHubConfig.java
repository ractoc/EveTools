package com.ractoc.eve.crawler.config;

import com.ractoc.eve.crawler.reader.MarketHubItemReader;
import com.ractoc.eve.crawler.step.MarketHubStep;
import com.ractoc.eve.crawler.writer.MarketHubItemWriter;
import com.ractoc.eve.universe_client.api.MarketHubResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarketHubConfig {

    @Bean
    MarketHubItemReader marketHubItemReader() {
        return new MarketHubItemReader();
    }

    @Bean
    MarketHubItemWriter marketHubItemWriter() {
        return new MarketHubItemWriter();
    }

    @Bean
    MarketHubStep marketHubStep() {
        return new MarketHubStep();
    }

    @Bean
    MarketHubResourceApi marketHubResourceApi() {
        return new MarketHubResourceApi();
    }

}
