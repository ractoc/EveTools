package com.ractoc.eve.user.controller;

import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.model.OAuthToken;
import com.ractoc.eve.user.response.UserResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"User Resource"}, value = "/api", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Region Resource", description = "Main entry point for the User API. " +
                "Lets other microservices request the user information based on the supplied EVE state.")
})
@RestController
@RequestMapping("/api")
@Validated
public class UserRestController {

    private UserHandler handler;
    private final Client client;

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
        refreshToken(request, eveState);
        return new ResponseEntity<>(handler.getUserByState(eveState), OK);
    }

    @ApiOperation(value = "Get user by EVE state. This user only contains the charactername and the eveState", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class),
            @ApiResponse(code = 403, message = "The user does not exist")
    })
    @GetMapping(value = "/username/{eveState}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getUserName(@PathVariable String eveState) {
        UserModel user = handler.getUserNameByState(eveState);
        if (user != null) {
            return new ResponseEntity<>(user, OK);
        } else {
            return new ResponseEntity<>(null, FORBIDDEN);
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
        handler.storeEveUserRegistration(eveState, oAuthToken, RequestUtils.getRemoteIP(request));
    }

}
