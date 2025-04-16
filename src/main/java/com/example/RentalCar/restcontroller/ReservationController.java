package com.example.RentalCar.restcontroller;

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

    // ------------------- Admin Routes ------------------------

    @GetMapping("/admin/reservations")
    public List<Reservation> getAllReservations() {
        return reservationRepo.findAll();
    }

    @GetMapping("/admin/reservation/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Integer id) {
        return reservationRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    LOGGER.warning("Reservation not found: ID " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/admin/reservation/{id}")
    public ResponseEntity<?> updateReservationAdmin(@PathVariable Integer id, @RequestBody Reservation updatedReservation, BindingResult result) {
        return handleUpdateReservation(id, updatedReservation, result, true);
    }

    @DeleteMapping("/admin/reservation/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        if (reservationRepo.existsById(id)) {
            reservationRepo.deleteById(id);
            LOGGER.info("Reservation deleted: ID " + id);
            return ResponseEntity.noContent().build();
        }
        LOGGER.warning("Attempted to delete non-existent reservation ID " + id);
        return ResponseEntity.notFound().build();
    }

    // ------------------- User Routes ------------------------

    @PostMapping("/user/reservation")
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation, BindingResult result) {
        LOGGER.info("Received reservation request");
        return handleCreateOrUpdateReservation(null, reservation, result, false);
    }

    @PutMapping("/user/reservation/{id}")
    public ResponseEntity<?> updateReservationUser(@PathVariable Integer id, @RequestBody Reservation reservation, BindingResult result) {
        return handleUpdateReservation(id, reservation, result, false);
    }

    // ------------------- Shared Logic ------------------------

    private ResponseEntity<?> handleUpdateReservation(Integer id, Reservation reservation, BindingResult result, boolean isAdmin) {
        return reservationRepo.findById(id)
                .map(existing -> handleCreateOrUpdateReservation(id, reservation, result, isAdmin))
                .orElseGet(() -> {
                    LOGGER.warning("Reservation not found for update: ID " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    private ResponseEntity<?> handleCreateOrUpdateReservation(Integer id, Reservation reservation, BindingResult result, boolean isAdmin) {
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
        if (reservation.getUser() == null || reservation.getUser().getId() == null) {
            LOGGER.warning("Missing user ID");
            return ResponseEntity.badRequest().body("User ID is required");
        }
        Optional<User> userOpt = userRepo.findById(reservation.getUser().getId());
        if (userOpt.isEmpty()) {
            LOGGER.warning("User not found: ID " + reservation.getUser().getId());
            return ResponseEntity.badRequest().body("User not found");
        }

        // Vehicle validation
        if (reservation.getVehicle() == null || reservation.getVehicle().getVehicleID() == null) {
            LOGGER.warning("Missing vehicle ID");
            return ResponseEntity.badRequest().body("Vehicle ID is required");
        }
        Optional<Vehicle> vehicleOpt = vehicleRepo.findById(reservation.getVehicle().getVehicleID());
        if (vehicleOpt.isEmpty()) {
            LOGGER.warning("Vehicle not found: ID " + reservation.getVehicle().getVehicleID());
            return ResponseEntity.badRequest().body("Vehicle not found");
        }

        reservation.setUser(userOpt.get());
        reservation.setVehicle(vehicleOpt.get());

        if (id != null) reservation.setReservationID(id);
        reservation.calculateTotalAmount();
        LOGGER.info((id != null ? "Updating" : "Creating") + " reservation: Total Amount = " + reservation.getTotalAmount());

        Reservation saved = reservationRepo.save(reservation);
        return new ResponseEntity<>(saved, id == null ? HttpStatus.CREATED : HttpStatus.OK);
    }
}
