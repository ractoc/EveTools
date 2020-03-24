package com.ractoc.eve.calculator.controller;

import com.ractoc.eve.calculator.handler.CalculatorHandler;
import com.ractoc.eve.calculator.response.BaseResponse;
import com.ractoc.eve.calculator.response.BlueprintResponse;
import com.ractoc.eve.calculator.response.ErrorResponse;
import com.ractoc.eve.calculator.service.ServiceException;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.user.filter.EveUserDetails;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorControllerTest implements WithAssertions {

    @InjectMocks
    private CalculatorController controller;

    @Mock
    private CalculatorHandler mockedHandler;
    @Mock
    private Authentication mockedAuthentication;
    @Mock
    private EveUserDetails mockedUserDetails;

    @Test
    void calculateBlueprintPrizes() {
        // Given
        BlueprintModel bpm = BlueprintModel.builder().id(100).build();
        when(mockedAuthentication.getPrincipal()).thenReturn(mockedUserDetails);
        when(mockedHandler.calculateBlueprintPrices(10, 20L, bpm, 30, mockedUserDetails)).thenReturn(bpm);

        // When
        ResponseEntity<BaseResponse> response = controller.calculateBlueprintPrizes(mockedAuthentication, 10, 20L, 30, bpm);

        // Then
        assertThat(response).isNotNull().extracting("statusCode").isEqualTo(HttpStatus.OK);
        BaseResponse body = response.getBody();
        assertThat(body).isNotNull().extracting("responseCode").isEqualTo(200);
        assertThat(body).isInstanceOf(BlueprintResponse.class);
        BlueprintResponse bpResponse = (BlueprintResponse) body;
        assertThat(bpResponse).extracting("blueprint").isEqualTo(bpm);
    }

    @Test
    void calculateBlueprintPrizesInternalServerError() {
        // Given
        BlueprintModel bpm = BlueprintModel.builder().id(100).build();
        when(mockedAuthentication.getPrincipal()).thenReturn(mockedUserDetails);
        when(mockedHandler.calculateBlueprintPrices(10, 20L, bpm, 30, mockedUserDetails)).thenThrow(new ServiceException("test exception"));

        // When
        ResponseEntity<BaseResponse> response = controller.calculateBlueprintPrizes(mockedAuthentication, 10, 20L, 30, bpm);

        // Then
        assertThat(response).isNotNull().extracting("statusCode").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        BaseResponse body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) body;
        assertThat(errorResponse).extracting("responseCode").isEqualTo(500);
        assertThat(errorResponse).extracting("message").isEqualTo("test exception");
    }
}
