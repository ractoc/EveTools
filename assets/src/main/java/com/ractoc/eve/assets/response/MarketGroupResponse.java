package com.ractoc.eve.assets.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.MarketGroupModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MarketGroupResponse extends BaseResponse {

    private MarketGroupModel marketGroup;

    public MarketGroupResponse(@NonNull HttpStatus responseCode, @NonNull MarketGroupModel marketGroup) {
        super(responseCode.value());
        this.marketGroup = marketGroup;
    }
}
