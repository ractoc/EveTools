package com.ractoc.eve.user.controller;

import com.ractoc.eve.domain.user.UserModel;
import com.ractoc.eve.user.handler.UserHandler;
import com.ractoc.eve.user.response.UserResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    public UserRestController(UserHandler handler) {
        this.handler = handler;
    }

    @ApiOperation(value = "Get user by EVE state", response = UserResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = UserModel.class)
    })
    @GetMapping(value = "/userdetails/{eveState}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getUserDetails(@PathVariable String eveState) {
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

}
