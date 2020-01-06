package com.ractoc.eve.user.config;

import com.ractoc.eve.user.db.UserApplication;
import com.ractoc.eve.user.db.UserApplicationBuilder;
import com.ractoc.eve.user.db.user.eve_user.user.UserManager;
import com.speedment.runtime.core.component.transaction.TransactionComponent;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public UserApplication getBackendApplication() {
        if (debug) {
            LOGGER.debug("connection parameters");
            LOGGER.debug("host: {}", host);
            LOGGER.debug("port: {}", port);
            LOGGER.debug("schema: {}", schema);
            LOGGER.debug("collation: {}", collation);
            LOGGER.debug("collationBinary: {}", collationBinary);
            return new UserApplicationBuilder()
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
        return new UserApplicationBuilder()
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
    public TransactionHandler getTransactionHandler(UserApplication app) {
        return app.getOrThrow(TransactionComponent.class).createTransactionHandler();
    }

    @Bean
    public UserManager getUserManager(UserApplication app) {
        return app.getOrThrow(UserManager.class);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder().indentOutput(true);
    }
}
