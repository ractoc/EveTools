package com.ractoc.eve.assets.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ractoc.eve.assets.db.AssetsApplication;
import com.ractoc.eve.assets.db.AssetsApplicationBuilder;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsManager;
import com.speedment.runtime.core.component.transaction.TransactionComponent;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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
    public AssetsApplication getBackendApplication() {
        if (debug) {
            LOGGER.debug("connection parameters");
            LOGGER.debug("host: {}", host);
            LOGGER.debug("port: {}", port);
            LOGGER.debug("schema: {}", schema);
            LOGGER.debug("collation: {}", collation);
            LOGGER.debug("collationBinary: {}", collationBinary);
            return new AssetsApplicationBuilder()
                    .withIpAddress(host)
                    .withPort(port)
                    .withUsername(username)
                    .withPassword(password)
                    .withSchema(schema)
                    .withParam("db.mysql.collationName", collation)
                    .withParam("db.mysql.binaryCollationName", collationBinary)
                    .withLogging(AssetsApplicationBuilder.LogType.STREAM)
                    .withLogging(AssetsApplicationBuilder.LogType.REMOVE)
                    .withLogging(AssetsApplicationBuilder.LogType.PERSIST)
                    .withLogging(AssetsApplicationBuilder.LogType.UPDATE)
                    .build();
        }
        return new AssetsApplicationBuilder()
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
    public TransactionHandler getTransactionHandler(AssetsApplication app) {
        return app.getOrThrow(TransactionComponent.class).createTransactionHandler();
    }

    @Bean
    public BlueprintManager getBlueprintManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintManager.class);
    }

    @Bean
    public BlueprintInventionMaterialsManager getBlueprintInventionMaterialsManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintInventionMaterialsManager.class);
    }

    @Bean
    public BlueprintInventionProductsManager getBlueprintInventionProductsManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintInventionProductsManager.class);
    }

    @Bean
    public BlueprintInventionSkillsManager getBlueprintInventionSkillsManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintInventionSkillsManager.class);
    }

    @Bean
    public BlueprintManufacturingMaterialsManager getBlueprintManufacturingMaterialsManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintManufacturingMaterialsManager.class);
    }

    @Bean
    public BlueprintManufacturingProductsManager getBlueprintManufacturingProductsManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintManufacturingProductsManager.class);
    }

    @Bean
    public BlueprintManufacturingSkillsManager getBlueprintManufacturingSkillsManager(AssetsApplication app) {
        return app.getOrThrow(BlueprintManufacturingSkillsManager.class);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder().modulesToInstall(new JavaTimeModule()).indentOutput(true);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
