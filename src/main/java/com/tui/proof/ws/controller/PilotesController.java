package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.request.UpdateOrderRequest;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class PilotesController {

    private final OrderService orderService;

    public PilotesController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/create")
    @ApiOperation(value = "Create a new order for an existing user", notes = "user, address must be provided")
    public ResponseEntity<PilotesOrderDtoResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity
                .created(URI.create("/order/pilotes"))
                .body(orderService.placeOrder(createOrderRequest));
    }

    @PutMapping("/order/update")
    @ApiOperation(value = "Update an order", notes = "you can only udpate after 5 minutes from the creation")
    public ResponseEntity<PilotesOrderDtoResponse> updateOrder(@Valid @RequestBody UpdateOrderRequest updateOrderRequest) {
        return ResponseEntity.ok(orderService.updateOrder(updateOrderRequest));
    }

}
