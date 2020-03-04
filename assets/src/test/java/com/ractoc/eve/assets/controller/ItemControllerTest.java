package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.TypeHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import com.ractoc.eve.assets.response.ItemNameResponse;
import com.ractoc.eve.assets.response.ItemResponse;
import com.ractoc.eve.assets.service.ServiceException;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.domain.assets.TypeModel;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest implements WithAssertions {

    @InjectMocks
    private ItemController controller;

    @Mock
    private TypeHandler mockedHandler;

    @Test
    void getItemName() {
        // Given
        when(mockedHandler.getItemName(25)).thenReturn("test name");

        // When
        ResponseEntity<BaseResponse> response = controller.getItemName(25);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ItemNameResponse.class);
        ItemNameResponse nameResponse = (ItemNameResponse) response.getBody();
        assertThat(nameResponse.getResponseCode()).isEqualTo(200);
        assertThat(nameResponse.getName()).isNotNull().isEqualTo("test name");
    }

    @Test
    void getItemNameNotFound() {
        // Given
        when(mockedHandler.getItemName(25)).thenThrow(new NoSuchElementException("test exception"));

        // When
        ResponseEntity<BaseResponse> response = controller.getItemName(25);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(404);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
    }

    @Test
    void getItemForBlueprint() {
        // Given
        when(mockedHandler.getItemForBlueprint(25)).thenReturn(ItemModel.builder().id(50).build());

        // When
        ResponseEntity<BaseResponse> response = controller.getItemForBlueprint(25);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ItemResponse.class);
        ItemResponse itemResponse = (ItemResponse) response.getBody();
        assertThat(itemResponse.getResponseCode()).isEqualTo(200);
        assertThat(itemResponse.getItem()).isNotNull();
        assertThat(itemResponse.getItem().getId()).isEqualTo(50);
    }

    @Test
    void getItemForBlueprintNotFound() {
        // Given
        when(mockedHandler.getItemForBlueprint(25)).thenThrow(new NoSuchElementException("test exception"));

        // When
        ResponseEntity<BaseResponse> response = controller.getItemForBlueprint(25);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(404);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
    }

    @Test
    void saveItems() {
        // Given
        List<TypeModel> types = Stream.of(TypeModel.builder().id(50).build()).collect(Collectors.toList());

        // When
        ResponseEntity<BaseResponse> response = controller.saveItems(types);

        // Then
        verify(mockedHandler).saveTypes(types);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(BaseResponse.class);
        BaseResponse baseResponse = response.getBody();
        assertThat(baseResponse.getResponseCode()).isEqualTo(201);
    }

    @Test
    void saveItemsInternalServerError() {
        // Given
        List<TypeModel> types = Stream.of(TypeModel.builder().id(50).build()).collect(Collectors.toList());
        doThrow(new ServiceException("test exception", null)).when(mockedHandler).saveTypes(types);

        // When
        ResponseEntity<BaseResponse> response = controller.saveItems(types);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getResponseCode()).isEqualTo(500);
        assertThat(errorResponse.getMessage()).isEqualTo("test exception");
    }

}
