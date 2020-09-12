package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.domain.fleetmanager.InviteModel;
import com.ractoc.eve.fleetmanager.handler.InviteHandler;
import com.ractoc.eve.fleetmanager.response.BaseResponse;
import com.ractoc.eve.fleetmanager.response.ErrorResponse;
import com.ractoc.eve.fleetmanager.response.InviteResponse;
import com.ractoc.eve.fleetmanager.service.ServiceException;
import com.ractoc.eve.user.filter.EveUserDetails;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Invite Resource"}, value = "/", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Invite Resource", description = "Main entry point for the Invite API. " +
                "Handles all invite actions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/invite")
@Validated
@Slf4j
public class InviteController {

    private final InviteHandler inviteHandler;

    @Autowired
    public InviteController(InviteHandler inviteHandler) {
        this.inviteHandler = inviteHandler;
    }

    @ApiOperation(value = "Invite corporation.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The invite was successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/corporation", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> inviteCorporation(@Valid @RequestBody InviteModel invite, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(new InviteResponse(CREATED,
                    inviteHandler.inviteCorporation(invite,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId(),
                            ((EveUserDetails) authentication.getPrincipal()).getAccessToken())),
                    CREATED);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Invite character.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The invite was successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/character", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> inviteCharacter(@Valid @RequestBody InviteModel invite, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(new InviteResponse(CREATED,
                    inviteHandler.inviteCharacter(invite,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId(),
                            ((EveUserDetails) authentication.getPrincipal()).getAccessToken())),
                    CREATED);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
