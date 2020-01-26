package com.ractoc.eve.assets.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemNameResponse extends BaseResponse {

    private String name;

    public ItemNameResponse(@NonNull HttpStatus responseCode, @NonNull String name) {
        super(responseCode.value());
        this.name = name;
    }
}
