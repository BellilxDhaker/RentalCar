package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.DTO.PaymentRequest;
import com.example.RentalCar.model.DTO.PaymentResult;
import com.example.RentalCar.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Endpoint to process payment
    @PostMapping("/user/payment")
    public ResponseEntity<PaymentResult> processPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResult result = paymentService.processPayment(paymentRequest);

        if ("SUCCESS".equals(result.getStatus())) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

}
