package com.ractoc.eve.universe.controller;

import com.ractoc.eve.domain.universe.RegionModel;
import com.ractoc.eve.universe.handler.RegionHandler;
import com.ractoc.eve.universe.response.BaseResponse;
import com.ractoc.eve.universe.response.ErrorResponse;
import com.ractoc.eve.universe.response.RegionListResponse;
import com.ractoc.eve.universe.response.RegionResponse;
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

@Api(tags = {"Region Resource"}, value = "/regions", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Region Resource", description = "Main entry point for the Region API. " +
                "Handles all related actions on the universe regions. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/regions")
@Validated
public class RegionController {

    private final RegionHandler regionHandler;

    @Autowired
    public RegionController(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
    }

    @ApiOperation(value = "Get list of regions", response = RegionListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed. This does not always mean regions were found.", response = RegionListResponse.class)
    })
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getRegionList() {
        return new ResponseEntity<>(
                new RegionListResponse(OK, regionHandler.getRegionList()), OK);
    }

    @ApiOperation(value = "Get region by ID", response = RegionResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = RegionResponse.class),
            @ApiResponse(code = 404, message = "The region does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "/{regionId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getRegion(@PathVariable Integer regionId) {
        try {
            return new ResponseEntity<>(
                        new RegionResponse(OK, regionHandler.getRegionById(regionId)), OK);
        } catch (NoSuchEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Save a region. This can result in a new region being created or an existing region being updated.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The region was successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveRegions(@Valid @RequestBody List<RegionModel> regions) {
        try {
            regionHandler.saveRegion(regions);
            return new ResponseEntity<>(new BaseResponse(CREATED.value()), OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
