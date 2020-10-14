package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.InviteModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InviteListResponse extends BaseResponse {

    private List<InviteModel> invites;

    public InviteListResponse(@NonNull HttpStatus responseCode, @NonNull List<InviteModel> invites) {
        super(responseCode.value());
        this.invites = invites;
    }
}
