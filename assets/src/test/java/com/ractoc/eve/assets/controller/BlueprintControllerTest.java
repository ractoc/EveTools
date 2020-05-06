package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.BlueprintHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.BlueprintListResponse;
import com.ractoc.eve.assets.response.BlueprintResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import com.ractoc.eve.assets.service.ServiceException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlueprintControllerTest implements WithAssertions {

    @InjectMocks
    private BlueprintController controller;

    @Mock
    private BlueprintHandler mockedHandler;
    @Mock
    private Authentication mockedAuthentication;

    @Test
    void getBlueprint() {
        // Given
        when(mockedHandler.getBlueprint(25)).thenReturn(BlueprintModel.builder().id(50).build());

        // When
        ResponseEntity<BaseResponse> response = controller.getBlueprint(25);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(BlueprintResponse.class);
        BlueprintResponse bpResponse = (BlueprintResponse) response.getBody();
        assertThat(bpResponse.getResponseCode()).isEqualTo(200);
        assertThat(bpResponse.getBlueprint()).isNotNull();
        assertThat(bpResponse.getBlueprint().getId()).isEqualTo(50);
    }

    @Test
    void getBlueprintNotFound() {
        // Given
        when(mockedHandler.getBlueprint(25)).thenThrow(new NoSuchElementException("test exception"));

        // When
        ResponseEntity<BaseResponse> response = controller.getBlueprint(25);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(404);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
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

        when(mockedAuthentication.getPrincipal()).thenReturn(user);
        when(mockedHandler.getBlueprintsForCharacter(user)).thenReturn(Stream.of(BlueprintModel.builder().id(50).build()).collect(Collectors.toList()));

        // When
        ResponseEntity<BaseResponse> response = controller.getBlueprintsForCharacter(mockedAuthentication);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(BlueprintListResponse.class);
        BlueprintListResponse bpResponse = (BlueprintListResponse) response.getBody();
        assertThat(bpResponse.getResponseCode()).isEqualTo(200);
        assertThat(bpResponse.getBlueprintList()).isNotNull().hasSize(1);
        assertThat(bpResponse.getBlueprintList().get(0)).isNotNull();
        assertThat(bpResponse.getBlueprintList().get(0).getId()).isEqualTo(50);
    }

    @Test
    void getBlueprintsForCharacterAccessDenied() {
        // Given
        when(mockedAuthentication.getPrincipal()).thenThrow(new AccessDeniedException("test exception"));

        // When
        ResponseEntity<BaseResponse> response = controller.getBlueprintsForCharacter(mockedAuthentication);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(401);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
    }

    @Test
    void getBlueprintsForCharacterInternalServerError() {
        // Given
        EveUserDetails user = new EveUserDetails("test_user",
                "test_password",
                new HashSet<>(),
                25,
                1234567890L,
                "127.0.0.1",
                "access_token");

        when(mockedAuthentication.getPrincipal()).thenReturn(user);
        when(mockedHandler.getBlueprintsForCharacter(user)).thenThrow(new ServiceException("test exception", null));

        // When
        ResponseEntity<BaseResponse> response = controller.getBlueprintsForCharacter(mockedAuthentication);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(500);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
    }

    @Test
    void saveBlueprints() {
        // Given
        List<BlueprintModel> bps = Stream.of(BlueprintModel.builder().id(50).build()).collect(Collectors.toList());

        // When
        ResponseEntity<BaseResponse> response = controller.saveBlueprints(bps);

        // Then
        verify(mockedHandler).saveBlueprints(bps);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(BaseResponse.class);
        BaseResponse baseResponse = response.getBody();
        assertThat(baseResponse.getResponseCode()).isEqualTo(201);
    }

    @Test
    void saveBlueprintsInternalServerError() {
        // Given
        List<BlueprintModel> bps = Stream.of(BlueprintModel.builder().id(50).build()).collect(Collectors.toList());
        doThrow(new ServiceException("test exception", null)).when(mockedHandler).saveBlueprints(bps);

        // When
        ResponseEntity<BaseResponse> response = controller.saveBlueprints(bps);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(500);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
    }
}
