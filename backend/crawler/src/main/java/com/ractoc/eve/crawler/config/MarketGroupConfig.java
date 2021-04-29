package com.ractoc.eve.crawler.config;

import com.ractoc.eve.assets_client.api.MarketGroupResourceApi;
import com.ractoc.eve.crawler.reader.MarketGroupItemReader;
import com.ractoc.eve.crawler.step.MarketGroupsStep;
import com.ractoc.eve.crawler.writer.MarketGroupItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarketGroupConfig {

    @Bean
    MarketGroupItemReader marketGroupItemReader() {
        return new MarketGroupItemReader();
    }

    @Bean
    MarketGroupItemWriter marketGroupItemWriter() {
        return new MarketGroupItemWriter();
    }

    @Bean
    MarketGroupsStep marketGroupStep() {
        return new MarketGroupsStep();
    }

    @Bean
    MarketGroupResourceApi marketGroupResourceApi() {
        return new MarketGroupResourceApi();
    }
}
