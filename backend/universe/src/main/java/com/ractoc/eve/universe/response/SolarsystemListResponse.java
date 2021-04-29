package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.SolarsystemModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class SolarsystemListResponse extends BaseResponse {

    private List<SolarsystemModel> solarsystemList;

    public SolarsystemListResponse(HttpStatus responseCode, List<SolarsystemModel> solarsystemList) {
        super(responseCode.value());
        this.solarsystemList = solarsystemList;
    }
}
