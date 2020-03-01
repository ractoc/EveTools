package com.ractoc.eve.universe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ractoc.eve.universe.db.UniverseApplication;
import com.ractoc.eve.universe.db.UniverseApplicationBuilder;
import com.ractoc.eve.universe.db.universe.eve_universe.constellation.ConstellationManager;
import com.ractoc.eve.universe.db.universe.eve_universe.market_hubs.MarketHubsManager;
import com.ractoc.eve.universe.db.universe.eve_universe.region.RegionManager;
import com.ractoc.eve.universe.db.universe.eve_universe.solarsystem.SolarsystemManager;
import com.speedment.runtime.core.component.transaction.TransactionComponent;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import com.speedment.runtime.join.JoinBundle;
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
    public UniverseApplication getBackendApplication() {
        if (debug) {
            LOGGER.debug("connection parameters");
            LOGGER.debug("host: {}", host);
            LOGGER.debug("port: {}", port);
            LOGGER.debug("schema: {}", schema);
            LOGGER.debug("collation: {}", collation);
            LOGGER.debug("collationBinary: {}", collationBinary);
            return new UniverseApplicationBuilder()
                    .withBundle(JoinBundle.class)
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
        return new UniverseApplicationBuilder()
                .withBundle(JoinBundle.class)
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
    public TransactionHandler getTransactionHandler(UniverseApplication app) {
        return app.getOrThrow(TransactionComponent.class).createTransactionHandler();
    }

    @Bean
    public RegionManager getRegionManager(UniverseApplication app) {
        return app.getOrThrow(RegionManager.class);
    }

    @Bean
    public ConstellationManager getConstellationManager(UniverseApplication app) {
        return app.getOrThrow(ConstellationManager.class);
    }

    @Bean
    public SolarsystemManager getSolarsystemManager(UniverseApplication app) {
        return app.getOrThrow(SolarsystemManager.class);
    }

    @Bean
    public MarketHubsManager getMarketHubsManager(UniverseApplication app) {
        return app.getOrThrow(MarketHubsManager.class);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
