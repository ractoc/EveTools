package com.ractoc.eve.crawler.config;

import com.ractoc.eve.assets_client.api.ItemResourceApi;
import com.ractoc.eve.crawler.reader.TypeItemReader;
import com.ractoc.eve.crawler.step.TypesStep;
import com.ractoc.eve.crawler.writer.TypeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeConfig {

    @Bean
    TypeItemReader typeItemReader() {
        return new TypeItemReader();
    }

    @Bean
    TypeItemWriter typeItemWriter() {
        return new TypeItemWriter();
    }

    @Bean
    TypesStep typeStep() {
        return new TypesStep();
    }

    @Bean
    ItemResourceApi itemResourceApi() {
        return new ItemResourceApi();
    }
}
