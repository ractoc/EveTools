package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.AssetsApplication;
import com.ractoc.eve.assets.db.AssetsApplicationBuilder;
import com.ractoc.eve.assets.db.assets.eve_assets.type.Type;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.type.TypeManager;
import com.ractoc.eve.assets.db.assets.eve_assets.type.generated.GeneratedType;
import com.ractoc.eve.test.db.SpeedmentDBTestCase;
import com.speedment.runtime.core.ApplicationBuilder;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class TypeServiceTest extends SpeedmentDBTestCase implements WithAssertions {

    private TypeService service;

    private final AssetsApplication assetsApplication = new AssetsApplicationBuilder()
            .withPassword(PASSWORD)
            .withConnectionUrl(dbUrl)
            .withLogging(ApplicationBuilder.LogType.STREAM)
            .withLogging(ApplicationBuilder.LogType.REMOVE)
            .withLogging(ApplicationBuilder.LogType.PERSIST)
            .withLogging(ApplicationBuilder.LogType.UPDATE)
            .build();

    @BeforeAll
    static void setupDatabase() throws Exception {
        createDatabase("eve_assets", 1234, "sql/createTables.sql", "datasets/initial.xml");
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        destroyDatabase();
    }

    @BeforeEach
    void setUp() {
        service = new TypeService(assetsApplication.getOrThrow(TypeManager.class));
    }

    @Test
    void saveType() throws Exception {
        // Given
        Type type = new TypeImpl();
        type.setMarketGroupId(25);
        type.setName("test type");

        // When
        service.saveType(type);

        // Then
        compareTable("datasets/save.xml", "type");
    }


    @Test
    void clearAllTypes() throws Exception {
        // When
        service.clearAllTypes();

        // Then
        assertThat(tableRowCount("type")).isEqualTo(0);
    }

    @Test
    void getItemName() {
        // When
        String name = service.getItemName(100001);

        // Then
        assertThat(name).isNotNull().isEqualTo("more stuff");
    }

    @Test
    void getItemById() {
        // When
        Type item = service.getItemById(100001);

        // Then
        assertThat(item).isNotNull().extracting("name").isEqualTo("more stuff");
    }

    @Test
    void getItemsByNameMultiple() {
        // When
        List<String> result = service.getItemsByName("tuf").map(GeneratedType::getName).collect(Collectors.toList());

        // Then
        assertThat(result).containsExactly("stuff", "more stuff");
    }

    @Test
    void getItemsByNameSingle() {
        // When
        List<String> result = service.getItemsByName("ore").map(GeneratedType::getName).collect(Collectors.toList());

        // Then
        assertThat(result).containsExactly("more stuff");
    }
}
