package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.TypeHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import com.ractoc.eve.assets.response.ItemNameResponse;
import com.ractoc.eve.assets.response.ItemResponse;
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

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = {"Item Resource"}, value = "/item", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Item Resource", description = "Main entry point for the Item API. " +
                "Handles all related actions on the universe items. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/item")
@Validated
public class ItemController {

    private final TypeHandler typeHandler;

    @Autowired
    public ItemController(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
    }

    @ApiOperation(value = "Get item by ID", response = ItemNameResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ItemNameResponse.class),
            @ApiResponse(code = 404, message = "Item not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/name/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getItemName(@AuthenticationPrincipal Authentication authentication, @PathVariable("id") int itemId) {
        try {
            return new ResponseEntity<>(new ItemNameResponse(OK, typeHandler.getItemName(itemId)), OK);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get item by ID", response = ItemResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ItemResponse.class),
            @ApiResponse(code = 404, message = "Item not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/blueprint/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getItemForBlueprint(@AuthenticationPrincipal Authentication authentication, @PathVariable("id") int blueprintId) {
        try {
            return new ResponseEntity<>(new ItemResponse(OK, typeHandler.getItemForBlueprint(blueprintId)), OK);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }
}
