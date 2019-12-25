package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.ConstellationModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConstellationResponse extends BaseResponse {

    private final ConstellationModel constellation;

    public ConstellationResponse(HttpStatus responseCode, ConstellationModel constellation) {
        super(responseCode.value());
        this.constellation = constellation;
    }
}
