package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.fleetmanager.handler.TypeHandler;
import com.ractoc.eve.fleetmanager.response.BaseResponse;
import com.ractoc.eve.fleetmanager.response.TypeListResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Fleet Type Resource"}, value = "/types", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Fleet Types Resource", description = "Main entry point for the Fleet Types API. " +
                "Handles all the Fleet Type actions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/types")
@Validated
@Slf4j
public class TypeController extends BaseController {

    private final TypeHandler typeHandler;

    @Autowired
    public TypeController(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
    }

    @ApiOperation(value = "Get all fleet types", response = TypeListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = TypeListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getTypes() {
        return new ResponseEntity<>(
                new TypeListResponse(OK,
                        typeHandler.getTypes()
                )
                , OK);
    }
}
