package com.ractoc.eve.assets.handler;

import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.Blueprint;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint.BlueprintImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_materials.BlueprintInventionMaterialsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_products.BlueprintInventionProductsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_invention_skills.BlueprintInventionSkillsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterials;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_materials.BlueprintManufacturingMaterialsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProducts;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_products.BlueprintManufacturingProductsImpl;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkills;
import com.ractoc.eve.assets.db.assets.eve_assets.blueprint_manufacturing_skills.BlueprintManufacturingSkillsImpl;
import com.ractoc.eve.assets.service.BlueprintService;
import com.ractoc.eve.assets.service.TypeService;
import com.ractoc.eve.domain.assets.BlueprintMaterialModel;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.BlueprintProductModel;
import com.ractoc.eve.domain.assets.BlueprintSkillModel;
import com.ractoc.eve.jesi.model.GetCharactersCharacterIdBlueprints200Ok;
import com.ractoc.eve.user.filter.EveUserDetails;
import com.speedment.runtime.core.component.transaction.Transaction;
import com.speedment.runtime.core.component.transaction.TransactionHandler;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlueprintHandlerTest implements WithAssertions {

    @InjectMocks
    private BlueprintHandler handler;

    @Captor
    private ArgumentCaptor<Consumer<? super Transaction>> saveBlueprintsLambdaCaptor;
    @Captor
    private ArgumentCaptor<Blueprint> blueprintCaptor;
    @Captor
    private ArgumentCaptor<BlueprintInventionMaterials> blueprintInventionMaterialsCaptor;
    @Captor
    private ArgumentCaptor<BlueprintInventionProducts> blueprintInventionProductsCaptor;
    @Captor
    private ArgumentCaptor<BlueprintInventionSkills> blueprintInventionSkillsCaptor;
    @Captor
    private ArgumentCaptor<BlueprintManufacturingMaterials> blueprintManufacturingMaterialsCaptor;
    @Captor
    private ArgumentCaptor<BlueprintManufacturingProducts> blueprintManufacturingProductsCaptor;
    @Captor
    private ArgumentCaptor<BlueprintManufacturingSkills> blueprintManufacturingSkillsCaptor;

    @Mock
    private BlueprintService mockedBlueprintService;
    @Mock
    private TypeService mockedTypeService;
    @Mock
    private TransactionHandler mockedTransactionHandler;
    @Mock
    private Transaction mockedTransaction;

    @Test
    void getBlueprint() {
        // Given
        Blueprint bp = createBlueprint(50);
        Set<BlueprintInventionMaterials> invMats = createBpInvMats();
        Set<BlueprintInventionProducts> invProds = createBpInvProds();
        Set<BlueprintInventionSkills> invSkills = createBpInvSkills();
        Set<BlueprintManufacturingMaterials> manMats = createBpManMats();
        Set<BlueprintManufacturingProducts> manProds = createBpManProds();
        Set<BlueprintManufacturingSkills> manSkills = createBpManSkills();

        when(mockedBlueprintService.getBlueprint(25)).thenReturn(bp);
        when(mockedTypeService.getItemName(50)).thenReturn("test name");
        when(mockedBlueprintService.getInventionMaterials(25)).thenReturn(invMats);
        when(mockedBlueprintService.getInventionProducts(25)).thenReturn(invProds);
        when(mockedBlueprintService.getInventionSkills(25)).thenReturn(invSkills);
        when(mockedBlueprintService.getManufacturingMaterials(25)).thenReturn(manMats);
        when(mockedBlueprintService.getManufacturingProducts(25)).thenReturn(manProds);
        when(mockedBlueprintService.getManufacturingSkills(25)).thenReturn(manSkills);

        // When
        BlueprintModel model = handler.getBlueprint(25);

        // Then
        assertThat(model).isNotNull();
        assertThat(model.getId()).isEqualTo(50);
        assertThat(model.getInventionMaterials()).hasSize(1);
        assertThat(model.getInventionProducts()).hasSize(1);
        assertThat(model.getInventionSkills()).hasSize(1);
        assertThat(model.getManufacturingMaterials()).hasSize(1);
        assertThat(model.getManufacturingProducts()).hasSize(1);
        assertThat(model.getManufacturingSkills()).hasSize(1);
    }

    @Test
    void getBlueprintsForCharacter() {
        // Given
        EveUserDetails user = new EveUserDetails("test_user",
                "test_password",
                new HashSet<>(),
                25,
                1234567890L,
                "127.0.0.1",
                "access_token");
        when(mockedBlueprintService.getBlueprintsForCharacter(25, "access_token"))
                .thenReturn(Stream.of(createEsiBlueprint(50), createEsiBlueprint(100)));
        when(mockedTypeService.getItemName(50)).thenReturn("horse");
        when(mockedTypeService.getItemName(100)).thenReturn("baby");

        // When
        List<BlueprintModel> results = handler.getBlueprintsForCharacter(user);

        // Then
        assertThat(results).isNotNull().hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(100);
        assertThat(results.get(1).getId()).isEqualTo(50);
    }

    @Test
    void saveBlueprints() {
        // Given
        BlueprintModel bpModel = createBlueprintModel();

        // When
        handler.saveBlueprints(Stream.of(bpModel).collect(Collectors.toList()));

        // Then
        verify(mockedTransactionHandler).createAndAccept(saveBlueprintsLambdaCaptor.capture());
        Consumer<? super Transaction> lambda = saveBlueprintsLambdaCaptor.getValue();
        lambda.accept(mockedTransaction);
        verify(mockedBlueprintService).clearAllBlueprints();
        verify(mockedBlueprintService).saveBlueprint(blueprintCaptor.capture());
        assertThat(blueprintCaptor.getValue().getId()).isEqualTo(50);
        verify(mockedBlueprintService).saveInventionMaterial(blueprintInventionMaterialsCaptor.capture());
        assertThat(blueprintInventionMaterialsCaptor.getValue().getBlueprintId()).isEqualTo(50);
        assertThat(blueprintInventionMaterialsCaptor.getValue().getTypeId()).isEqualTo(10);
        verify(mockedBlueprintService).saveInventionProduct(blueprintInventionProductsCaptor.capture());
        assertThat(blueprintInventionProductsCaptor.getValue().getBlueprintId()).isEqualTo(50);
        assertThat(blueprintInventionProductsCaptor.getValue().getTypeId()).isEqualTo(20);
        verify(mockedBlueprintService).saveInventionSkill(blueprintInventionSkillsCaptor.capture());
        assertThat(blueprintInventionSkillsCaptor.getValue().getBlueprintId()).isEqualTo(50);
        assertThat(blueprintInventionSkillsCaptor.getValue().getTypeId()).isEqualTo(30);
        verify(mockedBlueprintService).saveManufacturingMaterial(blueprintManufacturingMaterialsCaptor.capture());
        assertThat(blueprintManufacturingMaterialsCaptor.getValue().getBlueprintId()).isEqualTo(50);
        assertThat(blueprintManufacturingMaterialsCaptor.getValue().getTypeId()).isEqualTo(10);
        verify(mockedBlueprintService).saveManufacturingProduct(blueprintManufacturingProductsCaptor.capture());
        assertThat(blueprintManufacturingProductsCaptor.getValue().getBlueprintId()).isEqualTo(50);
        assertThat(blueprintManufacturingProductsCaptor.getValue().getTypeId()).isEqualTo(20);
        verify(mockedBlueprintService).saveManufacturingSkill(blueprintManufacturingSkillsCaptor.capture());
        assertThat(blueprintManufacturingSkillsCaptor.getValue().getBlueprintId()).isEqualTo(50);
        assertThat(blueprintManufacturingSkillsCaptor.getValue().getTypeId()).isEqualTo(30);
        verify(mockedTransaction).commit();
    }

    private BlueprintModel createBlueprintModel() {
        BlueprintMaterialModel bpMatModel = BlueprintMaterialModel.builder().blueprintId(50).typeId(10).build();
        BlueprintProductModel bpProdModel = BlueprintProductModel.builder().blueprintId(50).typeId(20).build();
        BlueprintSkillModel bpSkillModel = BlueprintSkillModel.builder().blueprintId(50).typeId(30).build();
        return BlueprintModel.builder()
                .id(50)
                .inventionMaterials(Stream.of(bpMatModel).collect(Collectors.toSet()))
                .inventionProducts(Stream.of(bpProdModel).collect(Collectors.toSet()))
                .inventionSkills(Stream.of(bpSkillModel).collect(Collectors.toSet()))
                .manufacturingMaterials(Stream.of(bpMatModel).collect(Collectors.toSet()))
                .manufacturingProducts(Stream.of(bpProdModel).collect(Collectors.toSet()))
                .manufacturingSkills(Stream.of(bpSkillModel).collect(Collectors.toSet()))
                .build();
    }

    private Set<BlueprintManufacturingSkills> createBpManSkills() {
        Set<BlueprintManufacturingSkills> manSkills = new HashSet<>();
        BlueprintManufacturingSkills manSkill = new BlueprintManufacturingSkillsImpl();
        manSkill.setTypeId(30);
        manSkills.add(manSkill);
        return manSkills;
    }

    private Set<BlueprintManufacturingProducts> createBpManProds() {
        Set<BlueprintManufacturingProducts> manProds = new HashSet<>();
        BlueprintManufacturingProducts manProd = new BlueprintManufacturingProductsImpl();
        manProd.setTypeId(20);
        manProds.add(manProd);
        return manProds;
    }

    private Set<BlueprintManufacturingMaterials> createBpManMats() {
        Set<BlueprintManufacturingMaterials> manMats = new HashSet<>();
        BlueprintManufacturingMaterials manMat = new BlueprintManufacturingMaterialsImpl();
        manMat.setTypeId(10);
        manMats.add(manMat);
        return manMats;
    }

    private Set<BlueprintInventionSkills> createBpInvSkills() {
        Set<BlueprintInventionSkills> invSkills = new HashSet<>();
        BlueprintInventionSkills invSkill = new BlueprintInventionSkillsImpl();
        invSkill.setTypeId(30);
        invSkills.add(invSkill);
        return invSkills;
    }

    private Set<BlueprintInventionProducts> createBpInvProds() {
        Set<BlueprintInventionProducts> invProds = new HashSet<>();
        BlueprintInventionProducts invProd = new BlueprintInventionProductsImpl();
        invProd.setTypeId(20);
        invProds.add(invProd);
        return invProds;
    }

    private Set<BlueprintInventionMaterials> createBpInvMats() {
        Set<BlueprintInventionMaterials> invMats = new HashSet<>();
        BlueprintInventionMaterials invMat = new BlueprintInventionMaterialsImpl();
        invMat.setTypeId(10);
        invMats.add(invMat);
        return invMats;
    }

    private GetCharactersCharacterIdBlueprints200Ok createEsiBlueprint(int id) {
        GetCharactersCharacterIdBlueprints200Ok bp = new GetCharactersCharacterIdBlueprints200Ok();
        bp.setTypeId(id);
        return bp;
    }

    private Blueprint createBlueprint(int id) {
        Blueprint bp = new BlueprintImpl();
        bp.setId(id);
        return bp;
    }
}
