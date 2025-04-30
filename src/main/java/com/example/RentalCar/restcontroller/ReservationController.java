package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.DTO.ApiResponse;
import com.example.RentalCar.model.entities.Extras;
import com.example.RentalCar.model.entities.Reservation;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.entities.Vehicle;
import com.example.RentalCar.model.repo.ReservationRepo;
import com.example.RentalCar.model.repo.UserRepo;
import com.example.RentalCar.model.repo.VehicleRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class ReservationController {

    private static final Logger LOGGER = Logger.getLogger(ReservationController.class.getName());

    @Autowired
    private ReservationRepo reservationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VehicleRepo vehicleRepo;

    @GetMapping("/admin/reservations")
    public ResponseEntity<ApiResponse<List<Reservation>>> getAllReservations() {
        List<Reservation> reservations = reservationRepo.findAll();
        return ResponseEntity.ok(new ApiResponse<>("All reservations fetched successfully", reservations, null));
    }

    @GetMapping("/admin/reservation/{id}")
    public ResponseEntity<ApiResponse<Reservation>> getReservationById(@PathVariable Integer id) {
        return reservationRepo.findById(id)
                .map(res -> ResponseEntity.ok(new ApiResponse<>("Reservation found", res, null)))
                .orElseGet(() -> {
                    String msg = "Reservation not found: ID " + id;
                    LOGGER.warning(msg);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(null, null, msg));
                });
    }

    @PutMapping("/admin/reservation/{id}")
    public ResponseEntity<ApiResponse<Reservation>> updateReservationAdmin(
            @PathVariable Integer id,
            @RequestBody Reservation updatedReservation,
            BindingResult result) {
        return handleUpdateReservation(id, updatedReservation, result, true);
    }

    @DeleteMapping("/admin/reservation/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Integer id) {
        Optional<Reservation> reservationOpt = reservationRepo.findById(id);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            Vehicle vehicle = reservation.getVehicle();
            if (vehicle != null) {
                vehicle.setAvailability(true); // Make vehicle available again
                vehicleRepo.save(vehicle);
            }
            reservationRepo.deleteById(id);
            LOGGER.info("Reservation deleted: ID " + id);
            return ResponseEntity.ok(new ApiResponse<>("Reservation deleted successfully", null, null));
        }
        String msg = "Attempted to delete non-existent reservation ID " + id;
        LOGGER.warning(msg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(null, null, msg));
    }

    @PostMapping("/user/reservation")
    public ResponseEntity<ApiResponse<Reservation>> createReservation(
            @RequestBody Reservation reservation,
            BindingResult result) {
        LOGGER.info("Received reservation creation request");
        return handleCreateOrUpdateReservation(null, reservation, result, false);
    }

    @PutMapping("/user/reservation/{id}")
    public ResponseEntity<ApiResponse<Reservation>> updateReservationUser(
            @PathVariable Integer id,
            @RequestBody Reservation reservation,
            BindingResult result) {
        return handleUpdateReservation(id, reservation, result, false);
    }

    // ---------------- Shared Logic ---------------- //

    private ResponseEntity<ApiResponse<Reservation>> handleUpdateReservation(
            Integer id,
            Reservation reservation,
            BindingResult result,
            boolean isAdmin) {
        return reservationRepo.findById(id)
                .map(existing -> handleCreateOrUpdateReservation(id, reservation, result, isAdmin))
                .orElseGet(() -> {
                    String msg = "Reservation not found for update: ID " + id;
                    LOGGER.warning(msg);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(null, null, msg));
                });
    }

    private ResponseEntity<ApiResponse<Reservation>> handleCreateOrUpdateReservation(
            Integer id,
            Reservation reservation,
            BindingResult result,
            boolean isAdmin) {

        // Validation errors
        if (result.hasErrors()) {
            String errorMessages = result.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            LOGGER.warning("Validation errors: " + errorMessages);
            return ResponseEntity.badRequest().body(new ApiResponse<>(null, null, errorMessages));
        }

        // User validation
        if (reservation.getUser() == null || reservation.getUser().getId() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("User ID is required", null, "Missing user ID"));
        }
        Optional<User> userOpt = userRepo.findById(reservation.getUser().getId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("User not found", null, "Invalid user ID"));
        }

        // Vehicle validation
        if (reservation.getVehicle() == null || reservation.getVehicle().getVehicleID() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Vehicle ID is required", null, "Missing vehicle ID"));
        }
        Optional<Vehicle> vehicleOpt = vehicleRepo.findById(reservation.getVehicle().getVehicleID());
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Vehicle not found", null, "Invalid vehicle ID"));
        }

        Vehicle vehicle = vehicleOpt.get();

        // Check availability only if it's a new reservation or vehicle is changed
        if (id == null && !vehicle.getAvailability()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("Vehicle is not available", null, "The selected vehicle is already reserved"));
        }

        // Set validated entities
        reservation.setUser(userOpt.get());
        reservation.setVehicle(vehicle);
        if (id != null) {
            reservation.setReservationID(id);
        }

        // Handle extras
        Map<Extras, Integer> extras = reservation.getExtras();
        try {
            reservation.setExtras(new HashMap<>());
            if (extras != null) {
                for (Map.Entry<Extras, Integer> entry : extras.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0) {
                        reservation.addExtra(entry.getKey(), entry.getValue());
                    } else {
                        LOGGER.warning("Invalid extra: " + entry.getKey() + ", quantity: " + entry.getValue());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Failed to add extras: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid extras", null, e.getMessage()));
        }

        // Save reservation and mark vehicle as unavailable
        vehicle.setAvailability(false);
        vehicleRepo.save(vehicle);

        LOGGER.info((id != null ? "Updating" : "Creating") + " reservation: Total Amount = " + reservation.getTotalAmount());
        Reservation saved = reservationRepo.save(reservation);
        String msg = (id == null ? "Reservation created successfully" : "Reservation updated successfully");
        return ResponseEntity.status(id == null ? HttpStatus.CREATED : HttpStatus.OK)
                .body(new ApiResponse<>(msg, saved, null));
    }
}
