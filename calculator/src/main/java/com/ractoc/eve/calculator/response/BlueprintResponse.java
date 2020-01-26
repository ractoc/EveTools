package com.ractoc.eve.calculator.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.BlueprintModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BlueprintResponse extends BaseResponse {

    private BlueprintModel bp;

    public BlueprintResponse(@NonNull HttpStatus responseCode, @NonNull BlueprintModel bp) {
        super(responseCode.value());
        this.bp = bp;
    }
}
