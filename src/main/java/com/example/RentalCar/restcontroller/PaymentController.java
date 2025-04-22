package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.entities.Payment;
import com.example.RentalCar.model.entities.Reservation;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.repo.PaymentRepo;
import com.example.RentalCar.model.repo.ReservationRepo;
import com.example.RentalCar.model.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class PaymentController {

    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());

    @Autowired
    private PaymentRepo paymentRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ReservationRepo reservationRepository;

    // -------------------- USER ROUTES --------------------

    @PostMapping("/user/payment")
    public ResponseEntity<?> createPayment(@RequestBody Payment paymentRequest, BindingResult result) {
        LOGGER.info("Received payment request: " + paymentRequest);
        return handleCreateOrUpdatePayment(null, paymentRequest, result);
    }

    @GetMapping("/user/payments/{userId}")
    public ResponseEntity<?> getPaymentsByUser(@PathVariable Long userId) {
        if (userId == null) {
            LOGGER.warning("User ID is null");
            return ResponseEntity.badRequest().body("User ID is required");
        }
        List<Payment> payments = paymentRepository.findByUserId(userId);
        LOGGER.info("Retrieved " + payments.size() + " payments for user ID: " + userId);
        return ResponseEntity.ok(payments);
    }

    // -------------------- ADMIN ROUTES --------------------

    @GetMapping("/admin/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        LOGGER.info("Retrieved all payments: " + payments.size());
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/admin/payment/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody Payment paymentRequest, BindingResult result) {
        LOGGER.info("Received update request for payment ID: " + id + ", paymentRequest: " + paymentRequest);
        return paymentRepository.findById(id)
                .map(existing -> handleCreateOrUpdatePayment(id, paymentRequest, result))
                .orElseGet(() -> {
                    LOGGER.warning("Payment not found for update: ID " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/admin/payment/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {
        if (id == null) {
            LOGGER.warning("Payment ID is null");
            return ResponseEntity.badRequest().body("Payment ID is required");
        }
        if (!paymentRepository.existsById(id)) {
            LOGGER.warning("Attempted to delete non-existent payment ID: " + id);
            return ResponseEntity.notFound().build();
        }
        paymentRepository.deleteById(id);
        LOGGER.info("Payment deleted: ID " + id);
        return ResponseEntity.ok("Payment deleted");
    }

    // -------------------- SHARED LOGIC --------------------

    private ResponseEntity<?> handleCreateOrUpdatePayment(Integer id, Payment paymentRequest, BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            LOGGER.warning("Validation errors: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        // User validation
        if (paymentRequest.getUser() == null || paymentRequest.getUser().getId() == null) {
            LOGGER.warning("Missing user ID");
            return ResponseEntity.badRequest().body("User ID is required");
        }
        Optional<User> userOpt = userRepository.findById(paymentRequest.getUser().getId());
        if (userOpt.isEmpty()) {
            LOGGER.warning("User not found: ID " + paymentRequest.getUser().getId());
            return ResponseEntity.badRequest().body("User not found");
        }

        // Reservation validation
        if (paymentRequest.getReservation() == null || paymentRequest.getReservation().getReservationID() == null) {
            LOGGER.warning("Missing reservation ID");
            return ResponseEntity.badRequest().body("Reservation ID is required");
        }
        Optional<Reservation> reservationOpt = reservationRepository.findById(paymentRequest.getReservation().getReservationID());
        if (reservationOpt.isEmpty()) {
            LOGGER.warning("Reservation not found: ID " + paymentRequest.getReservation().getReservationID());
            return ResponseEntity.badRequest().body("Reservation not found");
        }

        // Create or update payment
        Payment payment = (id == null) ? new Payment() : paymentRepository.findById(id).orElse(new Payment());
        payment.setUser(userOpt.get());
        payment.setReservation(reservationOpt.get());

        if (id != null) {
            payment.setPaymentID(id);
        }

        Payment saved = paymentRepository.save(payment);
        LOGGER.info((id == null ? "Created" : "Updated") + " payment: ID = " + saved.getPaymentID());
        return new ResponseEntity<>(saved, id == null ? HttpStatus.CREATED : HttpStatus.OK);
    }
}