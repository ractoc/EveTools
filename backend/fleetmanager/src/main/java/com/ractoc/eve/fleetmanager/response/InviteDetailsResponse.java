package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.InvitationModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InviteDetailsResponse extends BaseResponse {

    private InvitationModel invite;

    public InviteDetailsResponse(@NonNull HttpStatus responseCode, @NonNull InvitationModel invite) {
        super(responseCode.value());
        this.invite = invite;
    }
}
