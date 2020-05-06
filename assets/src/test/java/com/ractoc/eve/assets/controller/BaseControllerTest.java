package com.ractoc.eve.assets.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseControllerTest implements WithAssertions {

    @Mock
    private JsonMappingException mockedJsonMappingException;
    @Mock
    private HttpInputMessage mockedHttpInputMessage;

    private BaseController controller = new BaseController();

    @Test
    void handleHttpMessageNotReadableException() {
        // Given
        Object from;
        JsonMappingException.Reference ref = new JsonMappingException.Reference("test from", "test field");
        when(mockedJsonMappingException.getPath()).thenReturn(Stream.of(ref).collect(Collectors.toList()));

        // When
        String result = controller.handleHttpMessageNotReadableException(new HttpMessageNotReadableException("test exception", mockedJsonMappingException, mockedHttpInputMessage));

        // Then
        assertThat(result).isNotNull().isEqualTo("test field invalid");
    }
}
