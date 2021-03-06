package com.corelogic.sc.controllers;


import com.corelogic.sc.exceptions.CartNotFoundException;
import com.corelogic.sc.exceptions.InsufficientProductInventoryException;
import com.corelogic.sc.exceptions.ItemNotFoundException;
import com.corelogic.sc.exceptions.ProductNotFoundException;
import com.corelogic.sc.requests.AddItemRequest;
import com.corelogic.sc.requests.RemoveItemFromCartRequest;
import com.corelogic.sc.responses.ItemExceptionResponse;
import com.corelogic.sc.responses.ItemResponse;
import com.corelogic.sc.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(value = "/item")
    public ResponseEntity<ItemResponse> item(@RequestBody AddItemRequest addItemRequest)
            throws CartNotFoundException, ProductNotFoundException, InsufficientProductInventoryException {
        ItemResponse itemResponse = itemService.addItem(addItemRequest);
        return ResponseEntity.ok(itemResponse);
    }

    @PostMapping(value = "/cart/item")
    public ResponseEntity<ItemResponse> item(@RequestBody RemoveItemFromCartRequest removeItemFromCartRequest) throws CartNotFoundException, ProductNotFoundException, ItemNotFoundException {
        return ResponseEntity.ok(itemService.removeItem(removeItemFromCartRequest));
    }

    @GetMapping(value = "/{cartName}")
    public ResponseEntity<List<ItemResponse>> items(@PathVariable("cartName") String cartName) throws CartNotFoundException {
        List<ItemResponse> itemResponses = itemService.retrieveItems(cartName);
        return ResponseEntity.ok(itemResponses);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ItemExceptionResponse> cartNotFound(CartNotFoundException exception) {
        return new ResponseEntity<>(new ItemExceptionResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ItemExceptionResponse> productNotFound(ProductNotFoundException exception) {
        return new ResponseEntity<>(new ItemExceptionResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientProductInventoryException.class)
    public ResponseEntity<ItemExceptionResponse> insufficientProductInventory(InsufficientProductInventoryException exception) {
        return new ResponseEntity<>(new ItemExceptionResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ItemExceptionResponse> itemNotFound(ItemNotFoundException exception) {
        return new ResponseEntity<>(new ItemExceptionResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
