package com.samnang.ecommerce.service.impl;

import com.samnang.ecommerce.payload.StripePaymentDTO;
import com.samnang.ecommerce.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDTO stipePaymentDTO) throws StripeException {
        // Creates a Payment Intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(stipePaymentDTO.getAmount())
                .setCurrency(stipePaymentDTO.getCurrency())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);

    }
}
