package com.ractoc.eve.assets.service;

import com.ractoc.eve.assets.db.AssetsApplication;
import com.ractoc.eve.assets.db.AssetsApplicationBuilder;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsManager;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsManager;
import com.ractoc.eve.jesi.ApiException;
import com.ractoc.eve.jesi.api.CharacterApi;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import com.ractoc.eve.test.db.SpeedmentDBTestCase;
import com.speedment.runtime.core.ApplicationBuilder.LogType;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlueprintServiceTest extends SpeedmentDBTestCase implements WithAssertions {

    private BlueprintService service;

    private AssetsApplication assetsApplication = new AssetsApplicationBuilder()
            .withPassword("root")
            .withConnectionUrl(dbUrl)
            .withLogging(LogType.STREAM)
            .withLogging(LogType.REMOVE)
            .withLogging(LogType.PERSIST)
            .withLogging(LogType.UPDATE)
            .build();

    @Mock
    private CharacterApi mockedCharacterApi;

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
        service = new BlueprintService(assetsApplication.getOrThrow(BlueprintManager.class),
                assetsApplication.getOrThrow(BlueprintInventionMaterialsManager.class),
                assetsApplication.getOrThrow(BlueprintInventionProductsManager.class),
                assetsApplication.getOrThrow(BlueprintInventionSkillsManager.class),
                assetsApplication.getOrThrow(BlueprintManufacturingMaterialsManager.class),
                assetsApplication.getOrThrow(BlueprintManufacturingProductsManager.class),
                assetsApplication.getOrThrow(BlueprintManufacturingSkillsManager.class),
                mockedCharacterApi);
    }

    @Test
    void saveBlueprint() throws Exception {
        // Given
        Blueprint bp = new BlueprintImpl();
        bp.setMaxProductionLimit(2);
        bp.setCopyingTime(20);
        bp.setInventionTime(200);
        bp.setManufacturingTime(2000);
        bp.setResearchMaterialTime(20000);
        bp.setResearchTimeTime(200000);

        // When
        service.saveBlueprint(bp);

        // Then
        compareTable("datasets/save.xml", "blueprint");
    }

    @Test
    void getBlueprint() {
        // When
        Blueprint bp = service.getBlueprint(100000);

        // Then
        assertThat(bp).isNotNull().extracting("maxProductionLimit").isEqualTo(5);
    }

    @Test
    void saveInventionMaterial() throws Exception {
        // Given
        BlueprintInventionMaterials mat = new BlueprintInventionMaterialsImpl();
        mat.setBlueprintId(100001);
        mat.setTypeId(7);
        mat.setQuantity(70);

        // When
        service.saveInventionMaterial(mat);

        // Then
        compareTable("datasets/save.xml", "blueprint_invention_materials");
    }

    @Test
    void getInventionMaterials() {
        // When
        Set<BlueprintInventionMaterials> mats = service.getInventionMaterials(100000);

        // Then
        assertThat(mats).isNotNull().isNotEmpty().hasSize(2).extracting("typeId").containsExactlyInAnyOrder(20, 30);
    }

    @Test
    void saveInventionProduct() throws Exception {
        // Given
        BlueprintInventionProducts prods = new BlueprintInventionProductsImpl();
        prods.setBlueprintId(100001);
        prods.setTypeId(7);
        prods.setProbability(75);
        prods.setQuantity(70);

        // When
        service.saveInventionProduct(prods);

        // Then
        compareTable("datasets/save.xml", "blueprint_invention_products");
    }

    @Test
    void getInventionProducts() {
        // When
        Set<BlueprintInventionProducts> prods = service.getInventionProducts(100000);

        // Then
        assertThat(prods).isNotNull().isNotEmpty().hasSize(2).extracting("probability").containsExactlyInAnyOrder(25, 50);
    }

    @Test
    void saveInventionSkill() throws Exception {
        // Given
        BlueprintInventionSkills skills = new BlueprintInventionSkillsImpl();
        skills.setBlueprintId(100001);
        skills.setTypeId(7);
        skills.setLevel(3);

        // When
        service.saveInventionSkill(skills);

        // Then
        compareTable("datasets/save.xml", "blueprint_invention_skills");
    }

    @Test
    void getInventionSkills() {
        // When
        Set<BlueprintInventionSkills> skills = service.getInventionSkills(100000);

        // Then
        assertThat(skills).isNotNull().isNotEmpty().hasSize(2).extracting("level").containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void saveManufacturingMaterial() throws Exception {
        // Given
        BlueprintManufacturingMaterials mat = new BlueprintManufacturingMaterialsImpl();
        mat.setBlueprintId(100001);
        mat.setTypeId(7);
        mat.setQuantity(70);

        // When
        service.saveManufacturingMaterial(mat);

        // Then
        compareTable("datasets/save.xml", "blueprint_manufacturing_materials");
    }

    @Test
    void getManufacturingMaterials() {
        // When
        Set<BlueprintManufacturingMaterials> mats = service.getManufacturingMaterials(100000);

        // Then
        assertThat(mats).isNotNull().isNotEmpty().hasSize(2).extracting("typeId").containsExactlyInAnyOrder(20, 30);
    }

    @Test
    void saveManufacturingProduct() throws Exception {
        // Given
        BlueprintManufacturingProducts prods = new BlueprintManufacturingProductsImpl();
        prods.setBlueprintId(100001);
        prods.setTypeId(7);
        prods.setQuantity(70);

        // When
        service.saveManufacturingProduct(prods);

        // Then
        compareTable("datasets/save.xml", "blueprint_manufacturing_products");
    }

    @Test
    void getManufacturingProducts() {
        // When
        Set<BlueprintManufacturingProducts> prods = service.getManufacturingProducts(100000);

        // Then
        assertThat(prods).isNotNull().isNotEmpty().hasSize(2).extracting("typeId").containsExactlyInAnyOrder(20, 30);
    }

    @Test
    void saveManufacturingSkill() throws Exception {
        // Given
        BlueprintManufacturingSkills skills = new BlueprintManufacturingSkillsImpl();
        skills.setBlueprintId(100001);
        skills.setTypeId(7);
        skills.setLevel(3);

        // When
        service.saveManufacturingSkill(skills);

        // Then
        compareTable("datasets/save.xml", "blueprint_manufacturing_skills");
    }

    @Test
    void getManufacturingSkills() {
        // When
        Set<BlueprintManufacturingSkills> skills = service.getManufacturingSkills(100000);

        // Then
        assertThat(skills).isNotNull().isNotEmpty().hasSize(2).extracting("level").containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void clearAllBlueprints() throws Exception {
        // When
        service.clearAllBlueprints();

        // Then
        assertThat(tableRowCount("blueprint")).isEqualTo(0);
        assertThat(tableRowCount("blueprint_invention_materials")).isEqualTo(0);
        assertThat(tableRowCount("blueprint_invention_products")).isEqualTo(0);
        assertThat(tableRowCount("blueprint_invention_skills")).isEqualTo(0);
        assertThat(tableRowCount("blueprint_manufacturing_materials")).isEqualTo(0);
        assertThat(tableRowCount("blueprint_manufacturing_products")).isEqualTo(0);
        assertThat(tableRowCount("blueprint_manufacturing_skills")).isEqualTo(0);
    }

    @Test
    void getBlueprintsForCharacter() throws ApiException {
        // Given
        GetCharactersCharacterIdBlueprints200Ok okValue = new GetCharactersCharacterIdBlueprints200Ok();
        okValue.setTypeId(77);
        List<GetCharactersCharacterIdBlueprints200Ok> okValues = new ArrayList<>();
        okValues.add(okValue);
        when(mockedCharacterApi.getCharactersCharacterIdBlueprints(25, null, null, 1, "access_token")).thenReturn(okValues);

        // When
        Stream<GetCharactersCharacterIdBlueprints200Ok> result = service.getBlueprintsForCharacter(25, "access_token");

        // Then
        assertThat(result).hasSize(1).extracting("typeId").containsExactly(77);
    }

    @Test
    void getBlueprintsForCharacterForbidden() throws ApiException {
        // Given
        when(mockedCharacterApi.getCharactersCharacterIdBlueprints(25, null, null, 1, "access_token"))
                .thenThrow(new ApiException(403, "test exception"));

        // When
        Throwable thrown = catchThrowable(() -> service.getBlueprintsForCharacter(25, "access_token"));

        // Then
        assertThat(thrown)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access to the EVE SSO has been denied");
    }

    @Test
    void getBlueprintsForCharacterServiceException() throws ApiException {
        // Given
        when(mockedCharacterApi.getCharactersCharacterIdBlueprints(25, null, null, 1, "access_token"))
                .thenThrow(new ApiException(123, "test exception"));

        // When
        Throwable thrown = catchThrowable(() -> service.getBlueprintsForCharacter(25, "access_token"));

        // Then
        assertThat(thrown)
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Unable to retrieve Character Blueprints for character ID 25");
    }
}
