package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.BlueprintHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.BlueprintListResponse;
import com.ractoc.eve.assets.response.BlueprintResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import com.ractoc.eve.assets.service.ServiceException;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.user.filter.EveUserDetails;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;
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
@Slf4j
public class BlueprintController extends BaseController {

    private final BlueprintHandler blueprintHandler;

    @Autowired
    public BlueprintController(BlueprintHandler blueprintHandler) {
        this.blueprintHandler = blueprintHandler;
    }

    @ApiOperation(value = "Get blueprint by ID", response = BlueprintResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = BlueprintResponse.class),
            @ApiResponse(code = 404, message = "Blueprint not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getBlueprint(@PathVariable("id") int blueprintId) {
        try {
            return new ResponseEntity<>(new BlueprintResponse(OK, blueprintHandler.getBlueprint(blueprintId)), OK);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get character owned blueprints by character by ID", response = BlueprintListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = BlueprintListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "/character", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getBlueprintsForCharacter(@AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new BlueprintListResponse(OK,
                            blueprintHandler.getBlueprintsForCharacter((EveUserDetails) authentication.getPrincipal())
                    )
                    , OK);
        } catch (AccessDeniedException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(UNAUTHORIZED, e.getMessage()), UNAUTHORIZED);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Save all blueprints.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The blueprint were successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveBlueprints(@Valid @RequestBody List<BlueprintModel> bps) {
        try {
            blueprintHandler.saveBlueprints(bps);
            return new ResponseEntity<>(new BaseResponse(CREATED.value()), OK);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
