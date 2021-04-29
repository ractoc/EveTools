package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.RegionModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegionResponse extends BaseResponse {

    private final RegionModel region;

    public RegionResponse(HttpStatus responseCode, RegionModel region) {
        super(responseCode.value());
        this.region = region;
    }
}
