package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.domain.fleetmanager.InviteModel;
import com.ractoc.eve.fleetmanager.handler.InviteHandler;
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

@Api(tags = {"Invite Resource"}, value = "/invites", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Invite Resource", description = "Main entry point for the Invite API. " +
                "Handles all invite actions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/invites")
@Validated
@Slf4j
public class InviteController {

    private final InviteHandler inviteHandler;

    @Autowired
    public InviteController(InviteHandler inviteHandler) {
        this.inviteHandler = inviteHandler;
    }

    @ApiOperation(value = "Invite corporation.", response = InviteResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The invite was successfully created", response = InviteResponse.class),
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

    @ApiOperation(value = "Invite character.", response = InviteResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The invite was successfully created", response = InviteResponse.class),
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

    @ApiOperation(value = "Get Invite for character", response = InviteListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = InviteListResponse.class),
    })
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getInvitesForCharacter(@AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new InviteListResponse(OK, inviteHandler.getInvitesForCharacter(
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get Invite for character", response = InviteListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = InviteListResponse.class),
    })
    @DeleteMapping(value = "/{key}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteInvite(@PathVariable("key") String key, @AuthenticationPrincipal Authentication authentication) {
        try {
            inviteHandler.deleteInvite(key, ((EveUserDetails) authentication.getPrincipal()).getCharId());
            return new ResponseEntity<>(new BaseResponse(GONE.value()), OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get Invite for character", response = InviteListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = InviteListResponse.class),
    })
    @GetMapping(value = "/fleet/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getInvitesForFleet(@PathVariable("fleetId") Integer fleetId, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new InviteListResponse(OK, inviteHandler.getInvitesForFleet(fleetId,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get Invite by key", response = InviteDetailsResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = InviteDetailsResponse.class),
            @ApiResponse(code = 404, message = "Invite not found, or character is not allowed to see the invite data", response = ErrorResponse.class)
    })
    @GetMapping(value = "/{key}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getInvite(@PathVariable("key") String key, @AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new InviteDetailsResponse(OK, inviteHandler.getInvite(key,
                            ((EveUserDetails) authentication.getPrincipal()).getCharId())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
