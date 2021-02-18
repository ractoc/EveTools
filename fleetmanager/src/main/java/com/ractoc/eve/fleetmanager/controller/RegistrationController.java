package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.domain.fleetmanager.RegistrationModel;
import com.ractoc.eve.fleetmanager.handler.HandlerException;
import com.ractoc.eve.fleetmanager.handler.RegistrationHandler;
import com.ractoc.eve.fleetmanager.model.RegistrationConfirmation;
import com.ractoc.eve.fleetmanager.response.*;
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
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = InviteListResponse.class),
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

    @ApiOperation(value = "Get Registrations for fleet", response = InviteListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = InviteListResponse.class),
    })
    @GetMapping(value = "/fleet/{fleetId}/character/{characterId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getRegistrationsForFleetForCharacter(
            @PathVariable("fleetId") Integer fleetId,
            @PathVariable("characterId") Integer characterId,
            @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new RegistrationDetailsResponse(OK, registrationHandler.getRegistrationsForFleetForCharacter(
                            fleetId,
                            characterId,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
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
                    new RegistrationListResponse(OK, registrationHandler.registerForFleet(fleetId,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "update registration.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "The registration was successfully updated", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(value = "/{fleetId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> updateRegistration(
            @PathVariable("fleetId") Integer fleetId,
            @Valid @RequestBody RegistrationModel registration,
            @AuthenticationPrincipal Authentication authentication) {
        try {
            if (!fleetId.equals(registration.getFleetId())) {
                return new ResponseEntity<>(new ErrorResponse(CONFLICT, "Supplied ID doesn't match fleet id"), BAD_REQUEST);
            }
            registrationHandler.updateRegistration(registration,
                    ((EveUserDetails) authentication.getPrincipal()).getCharId());
            return new ResponseEntity<>(new BaseResponse(MOVED_PERMANENTLY.value()), ACCEPTED);
        } catch (ServiceException | HandlerException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }


    @ApiOperation(value = "Delete registration", response = BaseResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 410, message = "Registration removed", response = BaseResponse.class),
    })
    @DeleteMapping(value = "/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteRegistration(@PathVariable("fleetId") Integer fleetId, @AuthenticationPrincipal Authentication authentication) {
        try {
            registrationHandler.deleteRegistration(fleetId, ((EveUserDetails) authentication.getPrincipal()).getCharId());
            return new ResponseEntity<>(new BaseResponse(GONE.value()), OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
