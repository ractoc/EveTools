package com.ractoc.eve.assets.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.ItemModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class ItemListResponse extends BaseResponse {

    private List<ItemModel> itemList;

    public ItemListResponse(HttpStatus responseCode, List<ItemModel> itemList) {
        super(responseCode.value());
        this.itemList = itemList;
    }
}
