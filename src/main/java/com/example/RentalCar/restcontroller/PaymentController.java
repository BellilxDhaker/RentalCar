package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.DTO.ApiResponse;
import com.example.RentalCar.model.DTO.PaymentRequest;
import com.example.RentalCar.model.DTO.PaymentResult;
import com.example.RentalCar.model.repo.PaymentRepo;
import com.example.RentalCar.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private PaymentRepo paymentRepo;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Endpoint to process payment
    @PostMapping("/user/payment")
    public ResponseEntity<ApiResponse<PaymentResult>> processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResult result = paymentService.processPayment(paymentRequest);

            if ("COMPLETED".equalsIgnoreCase(result.getStatus())) {
                ApiResponse<PaymentResult> response = new ApiResponse<>("Payment processed successfully", result, null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<PaymentResult> response = new ApiResponse<>("Payment failed", result, null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            ApiResponse<PaymentResult> response = new ApiResponse<>("An error occurred while processing the payment", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/admin/payments")
    public ResponseEntity<ApiResponse<List<PaymentResult>>> getAllPayments() {
        try {
            List<PaymentResult> paymentResults = paymentService.getAllPayments();
            ApiResponse<List<PaymentResult>> response = new ApiResponse<>(
                    "All payment records retrieved successfully.",
                    paymentResults,
                    null
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<PaymentResult>> errorResponse = new ApiResponse<>(
                    null,
                    null,
                    "Error retrieving payments: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }


}