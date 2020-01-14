package com.ractoc.eve.character.controller;

import com.ractoc.eve.character.handler.CharacterHandler;
import com.ractoc.eve.character.response.BaseResponse;
import com.ractoc.eve.character.response.BlueprintListResponse;
import com.ractoc.eve.character.response.ErrorResponse;
import com.ractoc.eve.character.service.ServiceException;
import com.ractoc.eve.user.filter.EveUserDetails;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Character Resource"}, value = "/characters", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Character Resource", description = "Main entry point for the Character API. " +
                "Handles all related actions on the universe characters. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/characters")
@Validated
public class CharacterController {

    private final CharacterHandler characterHandler;

    @Autowired
    public CharacterController(CharacterHandler characterHandler) {
        this.characterHandler = characterHandler;
    }

    @ApiOperation(value = "Get character by ID", response = BlueprintListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = BlueprintListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "/blueprints", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getBlueprintsForCharacter(@AuthenticationPrincipal Authentication authentication) {
        try {
            return new ResponseEntity<>(
                    new BlueprintListResponse(OK,
                            characterHandler.getBlueprintsForCharacter((EveUserDetails) authentication.getPrincipal())
                    )
                    , OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
