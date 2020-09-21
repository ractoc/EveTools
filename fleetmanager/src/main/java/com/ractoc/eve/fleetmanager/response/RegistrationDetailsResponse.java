package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegistrationDetailsResponse extends BaseResponse {

    private RegistrationModel registration;

    public RegistrationDetailsResponse(@NonNull HttpStatus responseCode, RegistrationModel registration) {
        super(responseCode.value());
        this.registration = registration;
    }
}
