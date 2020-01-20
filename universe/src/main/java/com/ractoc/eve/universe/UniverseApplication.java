package com.ractoc.eve.universe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ractoc.eve"})
public class UniverseApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniverseApplication.class, args);
    }
}
