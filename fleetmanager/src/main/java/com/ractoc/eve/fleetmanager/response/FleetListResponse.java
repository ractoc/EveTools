package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.FleetModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class FleetListResponse extends BaseResponse {

    private List<FleetModel> fleetList;

    public FleetListResponse(HttpStatus responseCode, List<FleetModel> fleetList) {
        super(responseCode.value());
        this.fleetList = fleetList;
    }
}
