package com.ractoc.eve.universe.controller;

import com.ractoc.eve.domain.universe.SolarsystemModel;
import com.ractoc.eve.universe.handler.SolarsystemHandler;
import com.ractoc.eve.universe.response.BaseResponse;
import com.ractoc.eve.universe.response.SolarsystemListResponse;
import com.ractoc.eve.universe.response.SolarsystemResponse;
import com.ractoc.eve.universe.response.ErrorResponse;
import com.ractoc.eve.universe.service.DuplicateEntryException;
import com.ractoc.eve.universe.service.NoSuchEntryException;
import com.ractoc.eve.universe.service.ServiceException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Solarsystem Resource"}, value = "/solarsystems", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Solarsystem Resource", description = "Main entry point for the Solarsystem API. " +
                "Handles all related actions on the universe solarsystems. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/solarsystems")
@Validated
public class SolarsystemController {

    private final SolarsystemHandler solarsystemHandler;

    @Autowired
    public SolarsystemController(SolarsystemHandler solarsystemHandler) {
        this.solarsystemHandler = solarsystemHandler;
    }

    @ApiOperation(value = "Get list of solarsystems", response = SolarsystemListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed. This does not always mean solarsystems were found.", response = SolarsystemListResponse.class)
    })
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getSolarsystemList() {
        return new ResponseEntity<>(
                new SolarsystemListResponse(OK, solarsystemHandler.getSolarsystemList()), OK);
    }

    @ApiOperation(value = "Get solarsystem by ID", response = SolarsystemResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = SolarsystemResponse.class),
            @ApiResponse(code = 404, message = "The solarsystem does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "/{solarsystemId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getSolarsystem(@PathVariable Integer solarsystemId) {
        try {
            return new ResponseEntity<>(
                    new SolarsystemResponse(OK, solarsystemHandler.getSolarsystemById(solarsystemId)), OK);
        } catch (NoSuchEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Save a solarsystem. This can result in a new solarsystem being created or an existing solarsystem being updated.", response = SolarsystemResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The solarsystem was successfully created", response = SolarsystemResponse.class),
            @ApiResponse(code = 400, message = "Unable to create solarsystem, see body for more information", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> createSolarsystem(@Valid @RequestBody SolarsystemModel solarsystem) {
        try {
            return new ResponseEntity<>(new SolarsystemResponse(CREATED, solarsystemHandler.saveSolarsystem(solarsystem)), OK);
        } catch (DuplicateEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(CONFLICT, e.getMessage()), BAD_REQUEST);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
