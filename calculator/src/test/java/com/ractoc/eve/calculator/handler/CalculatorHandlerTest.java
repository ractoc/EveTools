package com.ractoc.eve.calculator.handler;

import com.ractoc.eve.assets_client.model.*;
import com.ractoc.eve.calculator.service.BlueprintService;
import com.ractoc.eve.calculator.service.CalculatorService;
import com.ractoc.eve.calculator.service.ItemService;
import com.ractoc.eve.user.filter.EveUserDetails;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorHandlerTest implements WithAssertions {

    @InjectMocks
    private CalculatorHandler handler;

    @Mock
    private BlueprintService mockedBlueprintService;
    @Mock
    private ItemService mockedItemService;
    @Mock
    private CalculatorService mockedCalculatorService;

    @Test
    void calculateBlueprintPrices() {
        // Given
        com.ractoc.eve.domain.assets.BlueprintModel bp = com.ractoc.eve.domain.assets.BlueprintModel.builder().id(50).build();
        BlueprintModel bpm = createBlueprintModel();
        ItemModel item = createItem();
        EveUserDetails userDetails = new EveUserDetails("test", "password", AuthorityUtils.createAuthorityList("USER"), 15, 123456789L, "127.0.0.1", "access_token");
        when(mockedBlueprintService.getBlueprint(50)).thenReturn(bpm);
        when(mockedItemService.getItemForBlueprint(50)).thenReturn(item);


        // When
        com.ractoc.eve.domain.assets.BlueprintModel result = handler.calculateBlueprintPrices(20, 30L, bp, 40, userDetails);

        // Then
        verify(mockedCalculatorService).calculateMaterialPrices(bp, 20, 30L, 40);
        verify(mockedCalculatorService).calculateItemPrices(any(com.ractoc.eve.domain.assets.ItemModel.class), same(20), same(30L), same(40));
        verify(mockedCalculatorService).calculateSalesTax(any(com.ractoc.eve.domain.assets.ItemModel.class), same(15), same("access_token"));
        verify(mockedCalculatorService).calculateBrokerFee(any(com.ractoc.eve.domain.assets.ItemModel.class), same(15), same("access_token"));
        verify(mockedCalculatorService).calculateJobInstallationCosts(bp, 15, "access_token");
        assertThat(result).isNotNull();
        assertThat(result.getManufacturingMaterials()).hasSize(1);
        assertThat(result.getManufacturingProducts()).hasSize(1);
        assertThat(result.getItem()).isNotNull().extracting("name").isEqualTo("random item");
    }

    private ItemModel createItem() {
        ItemModel item = new ItemModel();
        item.setId(100);
        item.setName("random item");
        return item;
    }

    private BlueprintModel createBlueprintModel() {
        BlueprintMaterialModel bpMatModel = new BlueprintMaterialModel();
        bpMatModel.setBlueprintId(50);
        bpMatModel.setTypeId(10);
        BlueprintProductModel bpProdModel = new BlueprintProductModel();
        bpProdModel.setBlueprintId(50);
        bpProdModel.setTypeId(20);
        BlueprintSkillModel bpSkillModel = new BlueprintSkillModel();
        bpSkillModel.setBlueprintId(50);
        bpSkillModel.setTypeId(30);
        BlueprintModel bpm = new BlueprintModel();
        bpm.setId(50);
        bpm.setInventionMaterials(Stream.of(bpMatModel).collect(Collectors.toList()));
        bpm.setInventionProducts(Stream.of(bpProdModel).collect(Collectors.toList()));
        bpm.setInventionSkills(Stream.of(bpSkillModel).collect(Collectors.toList()));
        bpm.setManufacturingMaterials(Stream.of(bpMatModel).collect(Collectors.toList()));
        bpm.setManufacturingProducts(Stream.of(bpProdModel).collect(Collectors.toList()));
        bpm.setManufacturingSkills(Stream.of(bpSkillModel).collect(Collectors.toList()));
        return bpm;
    }
}
