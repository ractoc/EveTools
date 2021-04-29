package com.ractoc.eve.fleetmanager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.fleetmanager.TypeModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class TypeListResponse extends BaseResponse {

    private final List<TypeModel> types;

    public TypeListResponse(HttpStatus responseCode, List<TypeModel> types) {
        super(responseCode.value());
        this.types = types;
    }
}
