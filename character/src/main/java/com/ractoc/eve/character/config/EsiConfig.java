package com.ractoc.eve.character.config;

import com.ractoc.eve.jesi.api.CharacterApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsiConfig {

    @Bean
    public CharacterApi getCharacterApi() {
        return new CharacterApi();
    }
}
