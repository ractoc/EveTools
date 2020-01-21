package com.ractoc.eve.character;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.ractoc.eve"})
public class CharacterApplication {
    public static void main(String[] args) {
        SpringApplication.run(CharacterApplication.class, args);
    }
}
