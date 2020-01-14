package com.ractoc.eve.character.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.assets.BlueprintModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class BlueprintListResponse extends BaseResponse {

    private List<BlueprintModel> bluePrintList;

    public BlueprintListResponse(HttpStatus responseCode, List<BlueprintModel> bluePrintList) {
        super(responseCode.value());
        this.bluePrintList = bluePrintList;
    }
}
