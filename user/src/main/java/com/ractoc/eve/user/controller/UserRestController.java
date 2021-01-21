package com.ractoc.eve.user.controller;

import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.model.OAuthToken;
import com.ractoc.eve.user.response.BaseResponse;
import com.ractoc.eve.user.response.UserResponse;
import com.ractoc.eve.user.service.ServiceException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"User Resource"}, value = "/api", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Region Resource", description = "Main entry point for the User API. " +
                "Lets other microservices request the user information based on the supplied EVE state.")
})
@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class UserRestController {

    private final Client client;
    private UserHandler handler;

    @Autowired
    public UserRestController(UserHandler handler, Client client) {
        this.handler = handler;
        this.client = client;
    }

    @ApiOperation(value = "Get user by EVE state", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class)
    })
    @GetMapping(value = "/userdetails/{eveState}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getUserDetails(HttpServletRequest request, @PathVariable String eveState) {
        try {
            refreshToken(request, eveState);
            return new ResponseEntity<>(handler.getUserByState(eveState), OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get user by EVE state. This user only contains the charactername and the eveState", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class),
            @ApiResponse(code = 403, message = "The user does not exist")
    })
    @GetMapping(value = "/user/{eveState}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getUser(@PathVariable String eveState) {
        try {
            UserModel user = handler.getUserNameByState(eveState);
            if (user != null) {
                return new ResponseEntity<>(user, OK);
            } else {
                return new ResponseEntity<>(null, FORBIDDEN);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Switch user by EVE state. This will remove the user linked to the provided eve state from the system.", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class),
            @ApiResponse(code = 403, message = "The user does not exist")
    })
    @DeleteMapping(value = "/user", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> switchUser(@RequestHeader(AUTHORIZATION) @NotNull String authorization) {
        try {
            handler.switchUser(StringUtils.removeStart(authorization, "Bearer").trim());
            return new ResponseEntity<>(new BaseResponse(GONE.value()), OK);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
    }

    private void refreshToken(HttpServletRequest request, String eveState) {
        String refreshToken = handler.getRefreshTokenForState(eveState);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        OAuthToken oAuthToken = client.target("https://login.eveonline.com/v2/oauth/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(formData), new GenericType<OAuthToken>() {
                });
        handler.storeEveUserRegistration(eveState, oAuthToken);
    }

}
