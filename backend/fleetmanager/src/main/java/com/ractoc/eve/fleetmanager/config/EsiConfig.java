package com.ractoc.eve.fleetmanager.config;

import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.api.CorporationApi;
import com.ractoc.eve.jesi.api.MailApi;
import com.ractoc.eve.jesi.api.UniverseApi;
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

    @Bean
    public MailApi getMailApi() {
        return new MailApi();
    }

    @Bean
    public UniverseApi getUniverseApi() {
        return new UniverseApi();
    }
}
