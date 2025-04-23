package com.example.RentalCar.service;

import com.example.RentalCar.model.DTO.PaymentRequest;
import com.example.RentalCar.model.DTO.PaymentResult;
import com.example.RentalCar.model.entities.Payment;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.repo.PaymentRepo;
import com.example.RentalCar.model.repo.UserRepo;
import com.squareup.square.SquareClient;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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
                .environment(environment.equals("sandbox") ? com.squareup.square.Environment.SANDBOX : com.squareup.square.Environment.PRODUCTION)
                .accessToken(accessToken)
                .build();
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public PaymentResult processPayment(PaymentRequest dto) {
        try {
            // Build the payment request for Square API
            CreatePaymentRequest body = new CreatePaymentRequest.Builder(
                    dto.getSourceId(),
                    UUID.randomUUID().toString()
            ).build();

            // Call Square API to process payment
            CreatePaymentResponse response = squareClient.getPaymentsApi().createPayment(body);
            com.squareup.square.models.Payment payment = response.getPayment();

            // Retrieve user details from the repository
            User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

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

            // Return a result with status and message
            return new PaymentResult(payment.getStatus(), payment.getId(), "Payment successful");

        }catch (ApiException e) {
            // Log the API exception message and print more detailed info
            System.out.println("API error details: " + e.getMessage());
            if (e.getErrors() != null) {
                e.getErrors().forEach(error -> System.out.println("Error: " + error.getDetail()));
            }
            return new PaymentResult("FAILED", null, "API Error: " + e.getMessage());
        } catch (Exception e) {
            // General error handling, log or return internal server error message
            return new PaymentResult("FAILED", null, "Internal error: " + e.getMessage());
        }
    }
}