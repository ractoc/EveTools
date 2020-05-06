package com.ractoc.eve.calculator.config;

import com.ractoc.eve.user.filter.UserAuthenticationFilter;
import com.ractoc.eve.user.filter.UserAuthenticationProvider;
import com.ractoc.eve.user_client.api.UserResourceApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final RequestMatcher PROTECTED_URLS = new AndRequestMatcher(
            new AntPathRequestMatcher("/**"),
            new NegatedRequestMatcher(new IpAddressMatcher("127.0.0.1"))
    );

    private UserAuthenticationProvider provider;

    public SecurityConfig(final UserAuthenticationProvider authenticationProvider) {
        this.provider = authenticationProvider;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .and()
                .authenticationProvider(provider)
                .addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers(PROTECTED_URLS)
                .authenticated()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }

    @Bean
    public UserAuthenticationFilter authenticationFilter() throws Exception {
        final UserAuthenticationFilter filter = new UserAuthenticationFilter(PROTECTED_URLS);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public UserAuthenticationProvider authenticationProvider(UserResourceApi userResourceApi) {
        return new UserAuthenticationProvider(userResourceApi);
    }

    @Bean
    public AuthenticationEntryPoint forbiddenEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedHeaders("Accept",
                        "Accept-Language",
                        "Content-Language",
                        "Content-Type",
                        "Last-Event-ID",
                        "DPR",
                        "Save-Data",
                        "Viewport-Width",
                        "Width",
                        "Authorization");
            }
        };
    }
}
