package com.ractoc.eve.assets.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.MarketGroupModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class MarketGroupListResponse extends BaseResponse {

    private List<MarketGroupModel> marketGroupList;

    public MarketGroupListResponse(HttpStatus responseCode, List<MarketGroupModel> marketGroupList) {
        super(responseCode.value());
        this.marketGroupList = marketGroupList;
    }
}
