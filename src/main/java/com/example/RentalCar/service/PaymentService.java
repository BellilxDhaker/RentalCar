package com.example.RentalCar.service;

import com.example.RentalCar.model.entities.Payment;
import com.example.RentalCar.model.entities.Reservation;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.repo.PaymentRepo;
import com.example.RentalCar.model.repo.ReservationRepo;
import com.example.RentalCar.model.repo.UserRepo;
import com.example.RentalCar.restcontroller.request.PaymentRequest;
import com.example.RentalCar.restcontroller.response.PaymentResult;
import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.Money;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final SquareClient squareClient;
    private final PaymentRepo paymentRepository;
    private final UserRepo userRepository;
    private final ReservationRepo reservationRepository;

    public PaymentService(
            @Value("${square.access.token}") String accessToken,
            @Value("${square.environment}") String environment,
            PaymentRepo paymentRepository,
            UserRepo userRepository,
            ReservationRepo reservationRepository
    ) {
        this.squareClient = new SquareClient.Builder()
                .accessToken(accessToken)
                .environment(environment.equalsIgnoreCase("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION)
                .build();
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

        public PaymentResult processPayment(PaymentRequest dto) {
            try {
                // Validate input
                if (dto.getUserId() == null) {
                    throw new IllegalArgumentException("User ID must be provided");
                }

                if (dto.getCurrency() == null || dto.getCurrency().isEmpty()) {
                    throw new IllegalArgumentException("Currency must be provided");
                }

                if (dto.getSourceId() == null || dto.getSourceId().isEmpty()) {
                    throw new IllegalArgumentException("Source ID must be provided");
                }

                // Retrieve user details
                User user = userRepository.findById(dto.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Retrieve reservation and get total amount
                Reservation reservation = reservationRepository.findById(dto.getReservationID())
                        .orElseThrow(() -> new RuntimeException("Reservation not found"));

                double amount = reservation.getTotalAmount();
                if (amount <= 0.0) {
                    throw new IllegalArgumentException("Reservation amount must be greater than 0");
                }

                // Build amountMoney for the payment (convert to cents)
                Money amountMoney = new Money.Builder()
                        .amount((long) (amount * 100)) // e.g., $10.00 -> 1000
                        .currency(dto.getCurrency())
                        .build();

                // Create the payment request for Square
                CreatePaymentRequest body = new CreatePaymentRequest.Builder(
                        dto.getSourceId(),
                        dto.getIdempotencyKey() != null ? dto.getIdempotencyKey() : UUID.randomUUID().toString()
                )
                        .amountMoney(amountMoney)
                        .build();

                // Process the payment with Square API
                CreatePaymentResponse response = squareClient.getPaymentsApi().createPayment(body);
                com.squareup.square.models.Payment payment = response.getPayment();

                // Save payment to database
                Payment savedPayment = new Payment();
                savedPayment.setAmount(amount);
                savedPayment.setCurrency(dto.getCurrency());
                savedPayment.setTransactionId(payment.getId());
                savedPayment.setSquarePaymentId(payment.getId());
                savedPayment.setStatus(payment.getStatus());
                savedPayment.setCreatedAt(LocalDateTime.now());
                savedPayment.setUser(user);
                savedPayment.setReservation(reservation);

                paymentRepository.save(savedPayment);
                // ✅ Update reservation status to CONFIRMED
                reservation.setStatus("CONFIRMED");
                reservationRepository.save(reservation);

                return new PaymentResult(payment.getStatus(), payment.getId(), "Payment successful", savedPayment);

            } catch (ApiException e) {
                // Log error
                System.out.println("API error details: " + e.getMessage());
                if (e.getErrors() != null) {
                    e.getErrors().forEach(error -> System.out.println(
                            "Error: Category=" + error.getCategory() + ", Code=" + error.getCode() + ", Detail=" + error.getDetail()
                    ));
                }
                String errorMessage = e.getErrors() != null && !e.getErrors().isEmpty()
                        ? e.getErrors().get(0).getDetail()
                        : e.getMessage();
                return new PaymentResult("FAILED", null, "API Error: " + errorMessage, null);

            } catch (IllegalArgumentException e) {
                return new PaymentResult("FAILED", null, "Validation Error: " + e.getMessage(), null);

            } catch (Exception e) {
                return new PaymentResult("FAILED", null, "Internal error: " + e.getMessage(), null);
            }
        }

    // Retrieve all payments
    public List<PaymentResult> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(payment -> new PaymentResult(
                        payment.getStatus(),
                        payment.getTransactionId(),
                        "Payment details retrieved successfully",
                        payment))
                .collect(Collectors.toList());
    }
}
