package com.ractoc.eve.assets.config;

import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.api.CorporationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsiConfig {

    @Bean
    public CharacterApi getCharacterApi() {
        return new CharacterApi();
    }

    @Bean
    public CorporationApi getCorporationApi() {
        return new CorporationApi();
    }
}
