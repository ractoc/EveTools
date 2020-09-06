package com.ractoc.eve.fleetmanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ractoc.eve.fleetmanager.db.FleetmanagerApplication;
import com.ractoc.eve.fleetmanager.db.FleetmanagerApplicationBuilder;
import com.ractoc.eve.fleetmanager.db.fleetmanager.eve_fleetmanager.fleet.FleetManager;
import com.speedment.runtime.core.component.transaction.TransactionComponent;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static com.speedment.runtime.core.ApplicationBuilder.LogType.*;

@Configuration
public class SpeedmentConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedmentConfiguration.class);

    @Value("${dbms.host}")
    private String host;
    @Value("${dbms.port}")
    private int port;
    @Value("${dbms.schema}")
    private String schema;
    @Value("${dbms.username}")
    private String username;
    @Value("${dbms.password}")
    private String password;
    @Value("${dbms.collation}")
    private String collation;
    @Value("${dbms.collation.binary}")
    private String collationBinary;
    @Value("${debug}")
    private boolean debug;

    @Bean
    public FleetmanagerApplication getBackendApplication() {
        if (debug) {
            LOGGER.debug("connection parameters");
            LOGGER.debug("host: {}", host);
            LOGGER.debug("port: {}", port);
            LOGGER.debug("schema: {}", schema);
            LOGGER.debug("collation: {}", collation);
            LOGGER.debug("collationBinary: {}", collationBinary);
            return new FleetmanagerApplicationBuilder()
                    .withIpAddress(host)
                    .withPort(port)
                    .withUsername(username)
                    .withPassword(password)
                    .withSchema(schema)
                    .withParam("db.mysql.collationName", collation)
                    .withParam("db.mysql.binaryCollationName", collationBinary)
                    .withLogging(STREAM)
                    .withLogging(REMOVE)
                    .withLogging(PERSIST)
                    .withLogging(UPDATE)
                    .build();
        }
        return new FleetmanagerApplicationBuilder()
                .withIpAddress(host)
                .withPort(port)
                .withUsername(username)
                .withPassword(password)
                .withSchema(schema)
                .withParam("db.mysql.collationName", collation)
                .withParam("db.mysql.binaryCollationName", collationBinary)
                .build();
    }

    @Bean
    public TransactionHandler getTransactionHandler(FleetmanagerApplication app) {
        return app.getOrThrow(TransactionComponent.class).createTransactionHandler();
    }

    @Bean
    public FleetManager getBlueprintManager(FleetmanagerApplication app) {
        return app.getOrThrow(FleetManager.class);
    }

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder().indentOutput(true);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
