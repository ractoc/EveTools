package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InviteResponse extends BaseResponse {

    private String inviteKey;

    public InviteResponse(@NonNull HttpStatus responseCode, @NonNull String inviteKey) {
        super(responseCode.value());
        this.inviteKey = inviteKey;
    }
}
