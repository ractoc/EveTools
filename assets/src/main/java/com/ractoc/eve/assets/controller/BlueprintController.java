package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.BlueprintHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.BlueprintResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Blueprint Resource"}, value = "/blueprint", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Blueprint Resource", description = "Main entry point for the Blueprint API. " +
                "Handles all related actions on the universe blueprints. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/blueprint")
@Validated
public class BlueprintController {

    @Autowired
    private BlueprintHandler blueprintHandler;

    @ApiOperation(value = "Get blueprint by ID", response = BlueprintResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = BlueprintResponse.class),
            @ApiResponse(code = 404, message = "Blueprint not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getBlueprint(@AuthenticationPrincipal Authentication authentication, @PathVariable("id") int blueprintId) {
        try {
            return new ResponseEntity<>(new BlueprintResponse(OK, blueprintHandler.getBlueprint(blueprintId)), OK);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }
}
