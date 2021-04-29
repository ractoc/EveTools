package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.MarketHubModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class MarketHubListResponse extends BaseResponse {

    private List<MarketHubModel> marketHubList;

    public MarketHubListResponse(HttpStatus responseCode, List<MarketHubModel> marketHubList) {
        super(responseCode.value());
        this.marketHubList = marketHubList;
    }
}
