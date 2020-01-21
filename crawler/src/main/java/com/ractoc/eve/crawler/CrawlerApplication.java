package com.ractoc.eve.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.ractoc.eve"})
public class CrawlerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(CrawlerApplication.class, args);
        System.exit(SpringApplication.exit(applicationContext));
    }

}
