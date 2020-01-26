package com.ractoc.eve.calculator.controller;

import com.ractoc.eve.calculator.handler.CalculatorHandler;
import com.ractoc.eve.calculator.response.BaseResponse;
import com.ractoc.eve.calculator.response.BlueprintResponse;
import com.ractoc.eve.calculator.response.ErrorResponse;
import com.ractoc.eve.calculator.service.ServiceException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/blueprint")
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
    @GetMapping(value = "/{bpId}/{regionId}/{locationId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> calculateBlueprintPizes(@AuthenticationPrincipal Authentication authentication
            , @PathVariable("bpId") Integer bpId
            , @PathVariable("regionId") Integer regionId
            , @PathVariable("locationId") Long locationId) {
        try {
            return new ResponseEntity<>(
                    new BlueprintResponse(OK,
                            calculatorHandler.calculateBlueprintPrices(bpId, regionId, locationId)
                    )
                    , OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
