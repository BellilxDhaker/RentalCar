package com.example.RentalCar.service;

import com.example.RentalCar.restcontroller.request.PaymentRequest;
import com.example.RentalCar.restcontroller.response.PaymentResult;
import com.example.RentalCar.model.entities.Payment;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.repo.PaymentRepo;
import com.example.RentalCar.model.repo.UserRepo;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final SquareClient squareClient;
    private final PaymentRepo paymentRepository;
    private final UserRepo userRepository;

    public PaymentService(
            @Value("${square.access.token}") String accessToken,
            @Value("${square.environment}") String environment,
            PaymentRepo paymentRepository,
            UserRepo userRepository
    ) {
        this.squareClient = new SquareClient.Builder()
                .accessToken(accessToken)
                .environment(environment.equalsIgnoreCase("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION)
                .build();
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public PaymentResult processPayment(PaymentRequest dto) {
        try {
            // Validate input
            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("User ID must be provided");
            }
            if (dto.getAmount() == null || dto.getAmount() <= 0) {
                throw new IllegalArgumentException("Amount must be provided and greater than 0");
            }
            if (dto.getCurrency() == null || dto.getCurrency().isEmpty()) {
                throw new IllegalArgumentException("Currency must be provided");
            }
            if (dto.getSourceId() == null || dto.getSourceId().isEmpty()) {
                throw new IllegalArgumentException("Source ID must be provided");
            }

            // Build amountMoney for the payment
            Money amountMoney = new Money.Builder()
                    .amount(dto.getAmount().longValue()) // Amount in cents (e.g., 1000 = $10.00)
                    .currency(dto.getCurrency()) // e.g., "USD"
                    .build();

            // Build the payment request for Square API
            CreatePaymentRequest body = new CreatePaymentRequest.Builder(
                    dto.getSourceId(),
                    dto.getIdempotencyKey() != null ? dto.getIdempotencyKey() : UUID.randomUUID().toString()
            )
                    .amountMoney(amountMoney)
                    .build();

            // Call Square API to process payment
            CreatePaymentResponse response = squareClient.getPaymentsApi().createPayment(body);
            com.squareup.square.models.Payment payment = response.getPayment();

            // Retrieve user details from the repository
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Save payment details to your repository
            Payment savedPayment = new Payment();
            savedPayment.setAmount(dto.getAmount());
            savedPayment.setCurrency(dto.getCurrency());
            savedPayment.setTransactionId(payment.getId());
            savedPayment.setSquarePaymentId(payment.getId());
            savedPayment.setStatus(payment.getStatus());
            savedPayment.setCreatedAt(LocalDateTime.now());
            savedPayment.setUser(user);

            paymentRepository.save(savedPayment);

            // Return a result with status and message, including the payment details directly
            return new PaymentResult(payment.getStatus(), payment.getId(), "Payment successful", savedPayment);

        } catch (ApiException e) {
            // Log detailed error information
            System.out.println("API error details: " + e.getMessage());
            if (e.getErrors() != null) {
                e.getErrors().forEach(error -> System.out.println(
                        "Error: Category=" + error.getCategory() + ", Code=" + error.getCode() + ", Detail=" + error.getDetail()
                ));
            }
            String errorMessage = e.getErrors() != null && !e.getErrors().isEmpty() ? e.getErrors().get(0).getDetail() : e.getMessage();
            return new PaymentResult("FAILED", null, "API Error: " + errorMessage,null);
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            return new PaymentResult("FAILED", null, "Validation Error: " + e.getMessage(), null);
        } catch (Exception e) {
            // General error handling
            return new PaymentResult("FAILED", null, "Internal error: " + e.getMessage(), null);
        }
    }
    // Method to retrieve all payments
    public List<PaymentResult> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();  // Retrieve all payments from the database

        return payments.stream()
                .map(payment -> new PaymentResult(
                        payment.getStatus(),
                        payment.getTransactionId(),
                        "Payment details retrieved successfully",
                        payment))
                .collect(Collectors.toList());
    }
}
