package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.MarketGroupHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import com.ractoc.eve.assets.response.MarketGroupListResponse;
import com.ractoc.eve.assets.response.MarketGroupResponse;
import com.ractoc.eve.assets.service.ServiceException;
import com.ractoc.eve.domain.assets.MarketGroupModel;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"MarketGroup Resource"}, value = "/group", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "MarketGroup Resource", description = "Main entry point for the MarketGroup API. " +
                "Handles all related actions on the universe marketGroups. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/group")
@Validated
@Slf4j
public class MarketGroupController extends BaseController {

    private final MarketGroupHandler marketGroupHandler;

    @Autowired
    public MarketGroupController(MarketGroupHandler marketGroupHandler) {
        this.marketGroupHandler = marketGroupHandler;
    }

    @ApiOperation(value = "Get marketGroups", response = MarketGroupListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = MarketGroupListResponse.class),
            @ApiResponse(code = 404, message = "MarketGroup not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getMarketGroups(@PathVariable("id") int parentGroupId) {
        try {
            return new ResponseEntity<>(new MarketGroupListResponse(OK, marketGroupHandler.getMarketGroups(parentGroupId)), OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get marketGroup by blueprint ID", response = MarketGroupResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = MarketGroupResponse.class),
            @ApiResponse(code = 404, message = "MarketGroup not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/blueprint/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getMarketGroupForBlueprint(@PathVariable("id") int blueprintId) {
        try {
            return new ResponseEntity<>(new MarketGroupResponse(OK, marketGroupHandler.getMarketGroupForBlueprint(blueprintId)), OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }

    @ApiOperation(value = "Save all marketGroups.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The marketGroups were successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveMarketGroups(@Valid @RequestBody List<MarketGroupModel> marketGroups) {
        try {
            marketGroupHandler.saveMarketGroups(marketGroups);
            return new ResponseEntity<>(new BaseResponse(CREATED.value()), OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
