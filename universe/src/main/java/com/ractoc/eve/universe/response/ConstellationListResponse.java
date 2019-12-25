package com.ractoc.eve.universe.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.universe.ConstellationModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class ConstellationListResponse extends BaseResponse {

    private List<ConstellationModel> constellationList;

    public ConstellationListResponse(HttpStatus responseCode, List<ConstellationModel> constellationList) {
        super(responseCode.value());
        this.constellationList = constellationList;
    }
}
