package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SolarsystemResponse extends BaseResponse {

    private final SolarsystemModel solarsystem;

    public SolarsystemResponse(HttpStatus responseCode, SolarsystemModel solarsystem) {
        super(responseCode.value());
        this.solarsystem = solarsystem;
    }
}
