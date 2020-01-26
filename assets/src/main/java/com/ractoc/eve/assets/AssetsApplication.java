package com.ractoc.eve.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.ractoc.eve"})
public class AssetsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssetsApplication.class, args);
    }
}
