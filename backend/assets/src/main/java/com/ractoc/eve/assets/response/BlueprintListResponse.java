package com.ractoc.eve.assets.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.BlueprintModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class BlueprintListResponse extends BaseResponse {

    private List<BlueprintModel> blueprintList;

    public BlueprintListResponse(HttpStatus responseCode, List<BlueprintModel> blueprintList) {
        super(responseCode.value());
        this.blueprintList = blueprintList;
    }
}
