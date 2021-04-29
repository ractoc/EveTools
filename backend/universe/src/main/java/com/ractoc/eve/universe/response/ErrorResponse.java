package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse extends BaseResponse {

    private final String message;

    public ErrorResponse(HttpStatus responseCode, String message) {
        super(responseCode.value());
        this.message = message;
    }
}
