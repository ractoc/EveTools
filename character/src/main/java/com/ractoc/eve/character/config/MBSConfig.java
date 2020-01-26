package com.ractoc.eve.character.config;

import com.ractoc.eve.assets_client.api.ItemResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MBSConfig {

    @Bean
    public ItemResourceApi itemResourceApi() {
        return new ItemResourceApi();
    }
}
