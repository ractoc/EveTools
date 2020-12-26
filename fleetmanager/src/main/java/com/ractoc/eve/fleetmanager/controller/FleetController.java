package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.domain.fleetmanager.FleetModel;
import com.ractoc.eve.fleetmanager.handler.FleetHandler;
import com.ractoc.eve.fleetmanager.handler.HandlerException;
import com.ractoc.eve.fleetmanager.model.FleetSearchParams;
import com.ractoc.eve.fleetmanager.response.BaseResponse;
import com.ractoc.eve.fleetmanager.response.ErrorResponse;
import com.ractoc.eve.fleetmanager.response.FleetListResponse;
import com.ractoc.eve.fleetmanager.response.FleetResponse;
import com.ractoc.eve.fleetmanager.service.ServiceException;
import com.ractoc.eve.user.filter.EveUserDetails;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Fleet Resource"}, value = "/", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Fleet Resource", description = "Main entry point for the Fleet API. " +
                "Handles all fleet management actions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/")
@Validated
@Slf4j
public class FleetController extends BaseController {

    private final FleetHandler fleetHandler;

    @Autowired
    public FleetController(FleetHandler fleetHandler) {
        this.fleetHandler = fleetHandler;
    }

    @ApiOperation(value = "Search fleets", response = FleetListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = FleetListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> searchFleets(@AuthenticationPrincipal Authentication authentication,
                                                     FleetSearchParams params) {
        try {
            return new ResponseEntity<>(
                    new FleetListResponse(OK,
                            fleetHandler.searchFleets(params, ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get fleet by ID", response = FleetResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = FleetResponse.class),
            @ApiResponse(code = 404, message = "Fleet not found, or character is not allowed to see the fleet data", response = ErrorResponse.class)
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getFleet(@PathVariable("id") Integer fleetId, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new FleetResponse(OK, fleetHandler.getFleet(fleetId,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Save fleet.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The fleet was successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveFleet(@Valid @RequestBody FleetModel fleet, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(new FleetResponse(CREATED,
                    fleetHandler.saveFleet(fleet,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())),
                    CREATED);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Save fleet.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "The fleet was successfully updated", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> updateFleet(@PathVariable("id") Integer fleetId, @Valid @RequestBody FleetModel fleet, @AuthenticationPrincipal Authentication authentication) {
        try {
            if (!fleetId.equals(fleet.getId())) {
                return new ResponseEntity<>(new ErrorResponse(CONFLICT, "Supplied ID doesn't match fleet id"), BAD_REQUEST);
            }
            fleetHandler.updateFleet(
                    fleet,
                    ((EveUserDetails) authentication.getPrincipal()).getCharId());
            return new ResponseEntity<>(new BaseResponse(MOVED_PERMANENTLY.value()), ACCEPTED);
        } catch (ServiceException | HandlerException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Delete fleet.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The fleet was successfully deleted", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteFleet(@PathVariable("id") Integer fleetId, @AuthenticationPrincipal Authentication authentication) {
        try {
            fleetHandler.deleteFleet(
                    fleetId,
                    ((EveUserDetails) authentication.getPrincipal()).getCharId());
            return new ResponseEntity<>(new BaseResponse(GONE.value()), OK);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
