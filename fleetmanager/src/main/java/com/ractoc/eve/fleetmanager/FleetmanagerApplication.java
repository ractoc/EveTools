package com.ractoc.eve.fleetmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.ractoc.eve"})
public class FleetmanagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FleetmanagerApplication.class, args);
    }
}
