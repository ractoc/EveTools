package com.ractoc.eve.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.user.UserModel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponse extends BaseResponse {

    private final UserModel user;

    public UserResponse(HttpStatus responseCode, @NonNull UserModel user) {
        super(responseCode.value());
        this.user = user;
    }

}
