package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.fleetmanager.handler.RegistrationHandler;
import com.ractoc.eve.fleetmanager.model.RegistrationConfirmation;
import com.ractoc.eve.fleetmanager.response.BaseResponse;
import com.ractoc.eve.fleetmanager.response.ErrorResponse;
import com.ractoc.eve.fleetmanager.response.FleetResponse;
import com.ractoc.eve.fleetmanager.response.RegistrationDetailsResponse;
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

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
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
public class RegistrationController {

    private final RegistrationHandler registrationHandler;

    @Autowired
    public RegistrationController(RegistrationHandler registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    @ApiOperation(value = "confirm registration", response = FleetResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Invite successfully accepted or denied.", response = FleetResponse.class),
            @ApiResponse(code = 404, message = "Invite not found, or character is not allowed to see the invite data", response = ErrorResponse.class)
    })
    @PostMapping(value = "/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> confirmationForFleet(@PathVariable("fleetId") Integer fleetId, @RequestBody RegistrationConfirmation confirmation, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new RegistrationDetailsResponse(OK, registrationHandler.registerForFleet(fleetId,
                            confirmation,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId(),
                            ((EveUserDetails) authentication.getPrincipal()).getAccessToken())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
