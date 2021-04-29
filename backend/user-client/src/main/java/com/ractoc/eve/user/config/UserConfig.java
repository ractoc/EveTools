package com.ractoc.eve.user.config;

import com.ractoc.eve.user_client.api.UserResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public UserResourceApi userResourceApi() {
        return new UserResourceApi();
    }

}
