package com.ractoc.eve.assets.controller;

import com.ractoc.eve.assets.handler.TypeHandler;
import com.ractoc.eve.assets.response.BaseResponse;
import com.ractoc.eve.assets.response.ErrorResponse;
import com.ractoc.eve.assets.response.ItemNameResponse;
import com.ractoc.eve.assets.response.ItemResponse;
import com.ractoc.eve.assets.service.ServiceException;
import com.ractoc.eve.domain.assets.TypeModel;
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

@Api(tags = {"Item Resource"}, value = "/item", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Item Resource", description = "Main entry point for the Item API. " +
                "Handles all related actions on the universe items. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RestController
@RequestMapping("/item")
@Validated
@Slf4j
public class ItemController extends BaseController {

    private final TypeHandler typeHandler;

    @Autowired
    public ItemController(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
    }

    @ApiOperation(value = "Get item name by item ID", response = ItemNameResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ItemNameResponse.class),
            @ApiResponse(code = 404, message = "Item not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/name/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getItemName(@PathVariable("id") int itemId) {
        try {
            return new ResponseEntity<>(new ItemNameResponse(OK, typeHandler.getItemName(itemId)), OK);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }

    @ApiOperation(value = "Get item by blueprint ID", response = ItemResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ItemResponse.class),
            @ApiResponse(code = 404, message = "Item not found", response = ErrorResponse.class)
    })
    @GetMapping(value = "/blueprint/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getItemForBlueprint(@PathVariable("id") int blueprintId) {
        try {
            return new ResponseEntity<>(new ItemResponse(OK, typeHandler.getItemForBlueprint(blueprintId)), OK);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        }
    }

    @ApiOperation(value = "Save all items.", response = BaseResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The items were successfully created", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> saveItems(@Valid @RequestBody List<TypeModel> items) {
        try {
            typeHandler.saveTypes(items);
            return new ResponseEntity<>(new BaseResponse(CREATED.value()), OK);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }
}
