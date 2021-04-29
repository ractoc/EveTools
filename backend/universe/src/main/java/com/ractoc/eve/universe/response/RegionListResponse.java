package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.RegionModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class RegionListResponse extends BaseResponse {

    private List<RegionModel> regionList;

    public RegionListResponse(HttpStatus responseCode, List<RegionModel> regionList) {
        super(responseCode.value());
        this.regionList = regionList;
    }
}
