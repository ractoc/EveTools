package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.RoleModel;
import com.ractoc.eve.domain.fleetmanager.TypeModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class RoleListResponse extends BaseResponse {

    private final List<RoleModel> roles;

    public RoleListResponse(HttpStatus responseCode, List<RoleModel> roles) {
        super(responseCode.value());
        this.roles = roles;
    }
}
