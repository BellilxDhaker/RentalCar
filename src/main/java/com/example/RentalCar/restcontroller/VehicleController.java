package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.DTO.ApiResponse;
import com.example.RentalCar.model.entities.Vehicle;
import com.example.RentalCar.model.repo.VehicleRepo;
import com.example.RentalCar.model.specifications.VehicleSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class VehicleController {
    private final Logger log = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleRepo vehicleRepository;

    // Get all vehicles
    @GetMapping("/auth/vehicles")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getAllVehicles() {
        try {
            List<Vehicle> vehicles = vehicleRepository.findAll();
            String message = vehicles.isEmpty() ? "No vehicles found" : "Retrieved " + vehicles.size() + " vehicles";
            log.info(message);
            return ResponseEntity.ok(new ApiResponse<>(message, vehicles, null));
        } catch (Exception e) {
            log.error("Failed to retrieve vehicles: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve vehicles",
                    e
            );
        }
    }

    // Get vehicle by ID
    @GetMapping("/auth/vehicle/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> getVehicleById(@PathVariable Integer id) {
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
            if (vehicle.isPresent()) {
                log.info("Retrieved vehicle with ID: {}", id);
                return ResponseEntity.ok(new ApiResponse<>("Vehicle retrieved successfully", vehicle.get(), null));
            } else {
                log.warn("Vehicle not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Vehicle with ID " + id + " not found", null, "Not found"));
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for vehicle retrieval: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to retrieve vehicle with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve vehicle",
                    e
            );
        }
    }

    // Add a new vehicle
    @PostMapping("/admin/vehicle")
    public ResponseEntity<ApiResponse<Vehicle>> addVehicle(@RequestBody Vehicle vehicle) {
        try {
            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            log.info("Vehicle created successfully with ID: {}", savedVehicle.getVehicleID());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Vehicle created successfully with ID: " + savedVehicle.getVehicleID(), savedVehicle, null));
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for vehicle creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating vehicle: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error creating vehicle",
                    e
            );
        }
    }

    // Delete a vehicle by ID
    @DeleteMapping("/admin/vehicle/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable  Integer id) {
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
            if (vehicle.isPresent()) {
                vehicleRepository.delete(vehicle.get());
                log.info("Vehicle deleted successfully with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>("Vehicle deleted successfully with ID: " + id, null, null));
            } else {
                log.warn("Vehicle not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Vehicle with ID " + id + " not found", null, "Not found"));
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for vehicle deletion: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete vehicle with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete vehicle",
                    e
            );
        }
    }

    // Check availability of a vehicle by ID
    @GetMapping("/auth/vehicle/{id}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(@PathVariable Integer id) {
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(id);
            if (vehicle.isPresent()) {
                log.info("Checked availability for vehicle with ID: {}", id);
                return ResponseEntity.ok(new ApiResponse<>(
                        "Availability checked for vehicle with ID: " + id,
                        vehicle.get().getAvailability(),
                        null
                ));
            } else {
                log.warn("Vehicle not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Vehicle with ID " + id + " not found", null, "Not found"));
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for vehicle availability check: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to check availability for vehicle with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to check vehicle availability",
                    e
            );
        }
    }

    // Update vehicle details by ID
    @PutMapping("/admin/update/vehicle/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> updateVehicle(
            @PathVariable Integer id,
            @RequestBody Vehicle updatedVehicle
    ) {
        try {
            Optional<Vehicle> existingVehicleOpt = vehicleRepository.findById(id);
            if (existingVehicleOpt.isPresent()) {
                Vehicle vehicle = existingVehicleOpt.get();

                // Only update fields that are not null in the request
                if (updatedVehicle.getBrand() != null) {
                    vehicle.setBrand(updatedVehicle.getBrand());
                }
                if (updatedVehicle.getCategory() != null) {
                    vehicle.setCategory(updatedVehicle.getCategory());
                }
                if (updatedVehicle.getImageURL() != null) {
                    vehicle.setImageURL(updatedVehicle.getImageURL());
                }
                if (updatedVehicle.getManufacturingYear() != null) {
                    vehicle.setManufacturingYear(updatedVehicle.getManufacturingYear());
                }
                if (updatedVehicle.getModel() != null) {
                    vehicle.setModel(updatedVehicle.getModel());
                }
                if (updatedVehicle.getNumberOfSeats() != null) {
                    vehicle.setNumberOfSeats(updatedVehicle.getNumberOfSeats());
                }
                if (updatedVehicle.getPricePerDay() != null) {
                    vehicle.setPricePerDay(updatedVehicle.getPricePerDay());
                }
                if (updatedVehicle.getAvailability() != null) {
                    vehicle.setAvailability(updatedVehicle.getAvailability());
                }

                Vehicle savedVehicle = vehicleRepository.save(vehicle);
                log.info("Vehicle updated successfully with ID: {}", id);
                return ResponseEntity.ok(new ApiResponse<>(
                        "Vehicle updated successfully with ID: " + id,
                        savedVehicle,
                        null
                ));
            } else {
                log.warn("Vehicle not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Vehicle with ID " + id + " not found", null, "Not found"));
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for vehicle update: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update vehicle with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update vehicle",
                    e
            );
        }
    }

    // Filter vehicles
    @GetMapping("/auth/filter")
    public ResponseEntity<ApiResponse<List<Vehicle>>> filterVehicles(
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "true") boolean sortAscending
    ) {
        try {
            Specification<Vehicle> spec = Specification
                    .where(VehicleSpecification.hasSeats(seats))
                    .and(VehicleSpecification.hasTransmission(transmission))
                    .and(VehicleSpecification.hasCategory(category))
                    .and(sortAscending ? VehicleSpecification.sortByPriceAsc() : VehicleSpecification.sortByPriceDesc());

            List<Vehicle> vehicles = vehicleRepository.findAll(spec);
            String message = vehicles.isEmpty() ? "No vehicles found matching criteria" : "Retrieved " + vehicles.size() + " vehicles";
            log.info(message);
            return ResponseEntity.ok(new ApiResponse<>(message, vehicles, null));
        } catch (Exception e) {
            log.error("Failed to filter vehicles: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to filter vehicles",
                    e
            );
        }
    }
}