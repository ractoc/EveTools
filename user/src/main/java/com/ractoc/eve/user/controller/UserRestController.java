package com.ractoc.eve.user.controller;

import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.model.AccessDeniedException;
import com.ractoc.eve.user.model.OAuthToken;
import com.ractoc.eve.user.response.BaseResponse;
import com.ractoc.eve.user.response.UserResponse;
import com.ractoc.eve.user.service.ServiceException;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
@Log4j2
public class UserRestController {

    private final Client client;
    private final UserHandler handler;

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
    public ResponseEntity<UserModel> getUserDetails(@PathVariable String eveState) {
        log.info("getting user details for state " + eveState);
        try {
            refreshToken(eveState);
            return new ResponseEntity<>(handler.getUserByState(eveState), OK);
        } catch (AccessDeniedException e) {
            log.error("unable to retreive user details", e);
            return new ResponseEntity<>(UNAUTHORIZED);
        } catch (ServiceException e) {
            log.error("unable to retreive user details", e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get user by EVE state", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class)
    })
    // should only be called from localhost, since that's where the other webservices also live.
    @PreAuthorize("#request.getRemoteAddr().equals(#request.getLocalAddr())")
    @GetMapping(value = "/evetools", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getEveTools(@P("request") HttpServletRequest request) {
        log.trace("getting evetools details for state ");
        try {
            UserModel evetools = handler.getEvetools();
            refreshToken(evetools.getEveState());
            return new ResponseEntity<>(evetools, OK);
        } catch (AccessDeniedException e) {
            log.error("unable to retreive user evetools", e);
            return new ResponseEntity<>(UNAUTHORIZED);
        } catch (ServiceException e) {
            log.error("unable to retreive user evetools", e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get user by EVE state. This user only contains the charactername and the eveState", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class),
            @ApiResponse(code = 403, message = "The user does not exist")
    })
    @GetMapping(value = "/user/{eveState}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getUser(@PathVariable String eveState) {
        log.trace("getting user for state " + eveState);
        try {
            UserModel user = handler.getUserNameByState(eveState);
            if (user != null) {
                return new ResponseEntity<>(user, OK);
            } else {
                return new ResponseEntity<>(FORBIDDEN);
            }
        } catch (AccessDeniedException e) {
            log.error("unable to retreive user", e);
            return new ResponseEntity<>(UNAUTHORIZED);
        } catch (ServiceException e) {
            e.printStackTrace();
            log.error("unable to retreive user", e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Lougout user by EVE state. This will remove the user linked to the provided eve state from the system.", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class),
            @ApiResponse(code = 403, message = "The user does not exist")
    })
    @DeleteMapping(value = "/user", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> logoutUser(@RequestHeader(AUTHORIZATION) @NotNull String authorization) {
        log.trace("logout user");
        try {
            handler.logoutUser(StringUtils.removeStart(authorization, "Bearer").trim());
            return new ResponseEntity<>(new BaseResponse(GONE.value()), OK);
        } catch (AccessDeniedException e) {
            log.error("unable to logout user", e);
            return new ResponseEntity<>(UNAUTHORIZED);
        } catch (ServiceException e) {
            log.error("unable to logout user", e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    private void refreshToken(String eveState) {
        log.trace("refresh token for state " + eveState);
        String refreshToken = handler.getRefreshTokenForState(eveState);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        OAuthToken oAuthToken = client.target("https://login.eveonline.com/v2/oauth/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(formData), new GenericType<>() {
                });
        handler.storeEveUserRegistration(eveState, oAuthToken);
    }

}
