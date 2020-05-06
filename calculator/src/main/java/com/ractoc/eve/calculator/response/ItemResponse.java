package com.ractoc.eve.calculator.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.ItemModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemResponse extends BaseResponse {

    private ItemModel item;

    public ItemResponse(@NonNull HttpStatus responseCode, @NonNull ItemModel item) {
        super(responseCode.value());
        this.item = item;
    }
}
