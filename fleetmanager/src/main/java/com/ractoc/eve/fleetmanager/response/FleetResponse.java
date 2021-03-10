package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.FleetModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FleetResponse extends BaseResponse {

    private FleetModel fleet;

    public FleetResponse(@NonNull HttpStatus responseCode, @NonNull FleetModel fleet) {
        super(responseCode.value());
        this.fleet = fleet;
    }
}
