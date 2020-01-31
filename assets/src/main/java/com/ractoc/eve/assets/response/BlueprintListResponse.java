package com.ractoc.eve.assets.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.eve.domain.character.BlueprintListModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@JsonInclude(Include.NON_EMPTY)
public class BlueprintListResponse extends BaseResponse {

    private List<BlueprintListModel> bluePrintList;

    public BlueprintListResponse(HttpStatus responseCode, List<BlueprintListModel> bluePrintList) {
        super(responseCode.value());
        this.bluePrintList = bluePrintList;
    }
}
