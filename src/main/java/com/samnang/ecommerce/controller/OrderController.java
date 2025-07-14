package com.samnang.ecommerce.controller;

import com.samnang.ecommerce.payload.OrderDTO;
import com.samnang.ecommerce.payload.OrderRequestDTO;
import com.samnang.ecommerce.payload.StripePaymentDTO;
import com.samnang.ecommerce.service.OrderService;
import com.samnang.ecommerce.service.StripeService;
import com.samnang.ecommerce.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;

    @Autowired
    private final AuthUtil authUtil;

    @Autowired
    private final StripeService stripeService;



    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDTO stipePaymentDTO) throws StripeException {
        PaymentIntent paymentIntent = stripeService.paymentIntent(stipePaymentDTO);

        return ResponseEntity.ok(paymentIntent.getClientSecret());
    }
}
