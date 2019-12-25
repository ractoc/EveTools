package com.ractoc.eve.universe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        exclude = SecurityAutoConfiguration.class,
        scanBasePackages = {"com.ractoc.eve.universe"})
public class UniverseApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniverseApplication.class, args);
    }
}
