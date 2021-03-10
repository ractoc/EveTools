package com.ractoc.eve.fleetmanager.controller;

import com.ractoc.eve.domain.fleetmanager.RoleModel;
import com.ractoc.eve.fleetmanager.handler.RoleHandler;
import com.ractoc.eve.fleetmanager.response.BaseResponse;
import com.ractoc.eve.fleetmanager.response.RoleListResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Fleet Type Resource"}, value = "/types", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Fleet Types Resource", description = "Main entry point for the Fleet Types API. " +
                "Handles all the Fleet Type actions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/roles")
@Validated
@Slf4j
public class RoleController extends BaseController {

    private final RoleHandler roleHandler;

    @Autowired
    public RoleController(RoleHandler roleHandler) {
        this.roleHandler = roleHandler;
    }

    @ApiOperation(value = "Get all fleet roles", response = RoleListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = RoleListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getRoles(@RequestParam(name = "fleetId", required = false) Integer fleetId) {
        return new ResponseEntity<>(
                new RoleListResponse(OK,
                        roleHandler.getRoles(fleetId)
                )
                , OK);
    }

    @ApiOperation(value = "Get all fleet roles", response = RoleListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = RoleListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getRolesForFleet(@PathVariable("fleetId") Integer fleetId) {
        return new ResponseEntity<>(
                new RoleListResponse(OK,
                        roleHandler.getRolesForFleet(fleetId)
                )
                , OK);
    }

    @ApiOperation(value = "Add role to fleet", response = RoleListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role successfully added", response = RoleListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(value = "/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> addRoleToFleet(@PathVariable("fleetId") Integer fleetId, @RequestBody RoleModel role) {
        return new ResponseEntity<>(
                new RoleListResponse(OK,
                        roleHandler.addRoleToFleet(role, fleetId)
                )
                , OK);
    }

    @ApiOperation(value = "Update role for fleet", response = RoleListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role successfully updated", response = RoleListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/{fleetId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> updateRoleToFleet(@PathVariable("fleetId") Integer fleetId, @RequestBody RoleModel role) {
        return new ResponseEntity<>(
                new RoleListResponse(OK,
                        roleHandler.updateRoleForFleet(role, fleetId)
                )
                , OK);
    }

    @ApiOperation(value = "remove role from fleet", response = RoleListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role successfully removed", response = RoleListResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(value = "/{fleetId}/{roleId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> removeRoleFromFleet(@PathVariable("roleId") Integer roleId, @PathVariable("fleetId") Integer fleetId) {
        return new ResponseEntity<>(
                new RoleListResponse(OK,
                        roleHandler.removeRoleFromFleet(roleId, fleetId)
                )
                , OK);
    }
}
