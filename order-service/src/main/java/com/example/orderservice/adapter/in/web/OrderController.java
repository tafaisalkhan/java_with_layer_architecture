package com.example.orderservice.adapter.in.web;

import com.example.common.query.GetByIdQuery;
import com.example.orderservice.application.port.in.CreateOrderCommand;
import com.example.orderservice.application.port.in.OrderResult;
import com.example.orderservice.application.port.in.usecase.CreateOrderUseCase;
import com.example.orderservice.application.port.in.usecase.GetOrderUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResult> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderResult result = createOrderUseCase.createOrder(command);
        return ResponseEntity.created(URI.create("/orders/" + result.orderId())).body(result);
    }

    @GetMapping("/{orderId}")
    public OrderResult getOrder(@PathVariable("orderId") UUID orderId) {
        System.out.println("get order");
        return getOrderUseCase.getOrder(new GetByIdQuery(orderId));
    }
}
