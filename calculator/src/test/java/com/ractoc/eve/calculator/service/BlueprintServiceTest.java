package com.ractoc.eve.calculator.service;

import com.ractoc.eve.assets_client.ApiException;
import com.ractoc.eve.assets_client.api.BlueprintResourceApi;
import com.ractoc.eve.assets_client.model.*;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlueprintServiceTest implements WithAssertions {

    @InjectMocks
    private BlueprintService service;

    @Mock
    private BlueprintResourceApi mockedBlueprintResourceApi;

    @Test
    void getBlueprint() throws ApiException {
        // Given
        BlueprintModel bpm = createBlueprintModel();
        BlueprintResponse bpr = new BlueprintResponse();
        bpr.setBlueprint(bpm);
        when(mockedBlueprintResourceApi.getBlueprint(25)).thenReturn(bpr);

        // When
        BlueprintModel result = service.getBlueprint(25);

        // Then
        assertThat(result).isNotNull().extracting("id").isEqualTo(50);
    }

    @Test
    void getBlueprintServiceException() throws ApiException {
        // Given
        when(mockedBlueprintResourceApi.getBlueprint(25)).thenThrow(new ApiException("test exception"));

        // When
        Throwable thrown = catchThrowable(() -> service.getBlueprint(25));

        // Then
        assertThat(thrown)
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Unable to retrieve Blueprint: 25");
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
