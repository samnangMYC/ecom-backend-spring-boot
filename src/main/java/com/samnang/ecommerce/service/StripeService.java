package com.samnang.ecommerce.service;

import com.samnang.ecommerce.payload.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    PaymentIntent paymentIntent(StripePaymentDTO stipePaymentDTO) throws StripeException;
}
