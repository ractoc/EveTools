package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.fleetmanager.handler.RegistrationHandler;
import com.ractoc.eve.fleetmanager.response.BaseResponse;
import com.ractoc.eve.fleetmanager.response.ErrorResponse;
import com.ractoc.eve.fleetmanager.response.InviteListResponse;
import com.ractoc.eve.fleetmanager.response.RegistrationListResponse;
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

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Registration Resource"}, value = "/registrations", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Registration Resource", description = "Main entry point for the Registration API. " +
                "Handles all registration actions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/registrations")
@Validated
@Slf4j
public class RegistrationController extends BaseController {

    private final RegistrationHandler registrationHandler;

    @Autowired
    public RegistrationController(RegistrationHandler registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    @ApiOperation(value = "Get Registrations for fleet", response = InviteListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = RegistrationListResponse.class),
    })
    @GetMapping(value = "/fleet/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getRegistrationsForFleet(@PathVariable("fleetId") Integer fleetId, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new RegistrationListResponse(OK, registrationHandler.getRegistrationsForFleet(fleetId,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Delete registration", response = BaseResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 410, message = "Registration removed", response = RegistrationListResponse.class),
    })
    @DeleteMapping(value = "/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteRegistration(@PathVariable("fleetId") Integer fleetId, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new RegistrationListResponse(GONE, registrationHandler.deleteRegistration(fleetId, ((EveUserDetails) authentication.getPrincipal()).getCharId()))
                            , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
