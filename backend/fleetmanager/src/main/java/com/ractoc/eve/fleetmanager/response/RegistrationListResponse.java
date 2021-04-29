package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegistrationListResponse extends BaseResponse {

    private List<RegistrationModel> registrations;

    public RegistrationListResponse(@NonNull HttpStatus responseCode, List<RegistrationModel> registrations) {
        super(responseCode.value());
        this.registrations = registrations;
    }
}
