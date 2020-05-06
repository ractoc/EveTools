package com.ractoc.eve.calculator.controller;

import com.ractoc.eve.calculator.handler.CalculatorHandler;
import com.ractoc.eve.calculator.response.BaseResponse;
import com.ractoc.eve.calculator.response.BlueprintResponse;
import com.ractoc.eve.calculator.response.ErrorResponse;
import com.ractoc.eve.calculator.response.ItemResponse;
import com.ractoc.eve.calculator.service.ServiceException;
import com.ractoc.eve.domain.assets.BlueprintModel;
import com.ractoc.eve.domain.assets.ItemModel;
import com.ractoc.eve.user.filter.EveUserDetails;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Calculator Resource"}, value = "/calculators", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Calculator Resource", description = "Main entry point for the Calculator API. " +
                "Handles all calculations. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/")
@Validated
public class CalculatorController {

    private final CalculatorHandler calculatorHandler;

    @Autowired
    public CalculatorController(CalculatorHandler calculatorHandler) {
        this.calculatorHandler = calculatorHandler;
    }

    @ApiOperation(value = "Get calculator by ID", response = BlueprintResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = BlueprintResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "blueprint/{buyRegionId}/{buyLocationId}/{sellRegionId}/{sellLocationId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> calculateBlueprintPrizes(@AuthenticationPrincipal Authentication authentication
            , @PathVariable("buyRegionId") Integer buyRegionId
            , @PathVariable("buyLocationId") Long buyLocationId
            , @PathVariable("sellRegionId") Integer sellRegionId
            , @PathVariable("sellLocationId") Long sellLocationId
            , @RequestParam(value = "runs", defaultValue = "1") Integer runs
            , @RequestBody BlueprintModel blueprint) {
        try {
            return new ResponseEntity<>(
                    new BlueprintResponse(OK,
                            calculatorHandler.calculateBlueprintPrices(buyRegionId, buyLocationId, sellRegionId, sellLocationId, blueprint, runs, (EveUserDetails) authentication.getPrincipal())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Get calculator by ID", response = ItemResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ItemResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "item/{buyRegionId}/{buyLocationId}/{sellRegionId}/{sellLocationId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> calculateItemPrizes(@AuthenticationPrincipal Authentication authentication
            , @PathVariable("buyRegionId") Integer buyRegionId
            , @PathVariable("buyLocationId") Long buyLocationId
            , @PathVariable("sellRegionId") Integer sellRegionId
            , @PathVariable("sellLocationId") Long sellLocationId
            , @RequestParam(value = "runs", defaultValue = "1") Integer runs
            , @RequestBody ItemModel item) {
        try {
            return new ResponseEntity<>(
                    new ItemResponse(OK,
                            calculatorHandler.calculateItemPrices(buyRegionId, buyLocationId, sellRegionId, sellLocationId, item, runs, (EveUserDetails) authentication.getPrincipal())
                    )
                    , OK);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
