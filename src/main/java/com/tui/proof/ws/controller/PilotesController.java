package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/order/pilotes")
    public ResponseEntity<PilotesOrderDtoResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity
                .created(URI.create("/order/pilotes"))
                .body(orderService.placeOrder(createOrderRequest));
    }

}
