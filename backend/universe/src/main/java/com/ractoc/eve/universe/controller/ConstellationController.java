package com.ractoc.eve.universe.controller;

import com.ractoc.eve.domain.universe.ConstellationModel;
import com.ractoc.eve.universe.handler.ConstellationHandler;
import com.ractoc.eve.universe.response.BaseResponse;
import com.ractoc.eve.universe.response.ConstellationListResponse;
import com.ractoc.eve.universe.response.ConstellationResponse;
import com.ractoc.eve.universe.response.ErrorResponse;
import com.ractoc.eve.universe.service.NoSuchEntryException;
import com.ractoc.eve.universe.service.ServiceException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Constellation Resource"}, value = "/constellations", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Constellation Resource", description = "Main entry point for the Constellation API. " +
                "Handles all related actions on the universe constellations. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/constellations")
@Validated
public class ConstellationController {

    private final ConstellationHandler constellationHandler;

    @Autowired
    public ConstellationController(ConstellationHandler constellationHandler) {
        this.constellationHandler = constellationHandler;
    }

    @ApiOperation(value = "Get list of constellations", response = ConstellationListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed. This does not always mean constellations were found.", response = ConstellationListResponse.class)
    })
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getConstellationList() {
        return new ResponseEntity<>(
                new ConstellationListResponse(OK, constellationHandler.getConstellationList()), OK);
    }

    @ApiOperation(value = "Get constellation by ID", response = ConstellationResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ConstellationResponse.class),
            @ApiResponse(code = 404, message = "The constellation does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "/{constellationId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getConstellation(@PathVariable Integer constellationId) {
        try {
            return new ResponseEntity<>(
                    new ConstellationResponse(OK, constellationHandler.getConstellationById(constellationId)), OK);
        } catch (NoSuchEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Save ass constellations. This can result in a new constellation being created or an existing constellation being updated.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The constellation was successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveConstellation(@Valid @RequestBody List<ConstellationModel> constellations) {
        try {
            constellationHandler.saveConstellations(constellations);
            return new ResponseEntity<>(new BaseResponse(CREATED.value()), OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
