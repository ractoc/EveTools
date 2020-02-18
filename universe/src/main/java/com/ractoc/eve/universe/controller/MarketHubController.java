package com.ractoc.eve.universe.controller;

import com.ractoc.eve.domain.universe.MarketHubModel;
import com.ractoc.eve.universe.handler.MarketHubHandler;
import com.ractoc.eve.universe.response.BaseResponse;
import com.ractoc.eve.universe.response.ErrorResponse;
import com.ractoc.eve.universe.response.MarketHubListResponse;
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

@Api(tags = {"MarketHub Resource"}, value = "/marketHubs", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "MarketHub Resource", description = "Main entry point for the MarketHub API. " +
                "Handles all related actions on the universe marketHubs. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/marketHubs")
@Validated
public class MarketHubController {

    private final MarketHubHandler marketHubHandler;

    @Autowired
    public MarketHubController(MarketHubHandler marketHubHandler) {
        this.marketHubHandler = marketHubHandler;
    }

    @ApiOperation(value = "Get list of marketHubs", response = MarketHubListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed. This does not always mean marketHubs were found.", response = MarketHubListResponse.class)
    })
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getMarketHubList() {
        return new ResponseEntity<>(
                new MarketHubListResponse(OK, marketHubHandler.getMarketHubList()), OK);
    }

    @ApiOperation(value = "Save marketHubs. This can result in a new marketHub being created or an existing marketHub being updated.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The marketHub was successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveMarketHubs(@Valid @RequestBody List<MarketHubModel> marketHubs) {
        try {
            marketHubHandler.saveMarketHubs(marketHubs);
            return new ResponseEntity<>(new BaseResponse(CREATED.value()), OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
