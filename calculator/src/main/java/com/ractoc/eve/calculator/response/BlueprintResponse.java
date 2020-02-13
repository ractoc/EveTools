package com.ractoc.eve.calculator.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.BlueprintModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BlueprintResponse extends BaseResponse {

    private BlueprintModel blueprint;

    public BlueprintResponse(@NonNull HttpStatus responseCode, @NonNull BlueprintModel blueprint) {
        super(responseCode.value());
        this.blueprint = blueprint;
    }
}
