package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.entities.Vehicle;
import com.example.RentalCar.model.repo.VehicleRepo;
import com.example.RentalCar.model.specifications.VehicleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class VehicleController {

    @Autowired
    private VehicleRepo vehicleRepository;

    // Get all vehicles
    @GetMapping("/auth/vehicles")
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        System.out.println("Fetched vehicles: " + vehicles);  // Log the vehicles
        return vehicles;
    }

    // Get vehicle by ID
    @GetMapping("/auth/vehicle/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Integer id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        return vehicle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Add a new vehicle
    @PostMapping("/admin/vehicle")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return ResponseEntity.status(201).body(savedVehicle); // Return created status
    }



    // Delete a vehicle by ID
    @DeleteMapping("/admin/vehicle/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Integer id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            vehicleRepository.delete(vehicle.get()); // Delete the vehicle
            return ResponseEntity.noContent().build(); // Return 204 (No Content)
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if the vehicle doesn't exist
        }
    }

    // Check availability of a vehicle by ID
    @GetMapping("/auth/vehicle/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable Integer id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get().getAvailability()); // Return the availability status
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if the vehicle doesn't exist
        }
    }
    // Update vehicle details by ID
    @PutMapping("/admin/update/vehicle/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Integer id, @RequestBody Vehicle updatedVehicle) {
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

            // Save the updated vehicle
            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            return ResponseEntity.ok(savedVehicle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/auth/filter")
    public List<Vehicle> filterVehicles(
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "true") boolean sortAscending
    ) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecification.hasSeats(seats))
                .and(VehicleSpecification.hasTransmission(transmission))
                .and(VehicleSpecification.hasCategory(category))
                .and(sortAscending ? VehicleSpecification.sortByPriceAsc() : VehicleSpecification.sortByPriceDesc());

        return vehicleRepository.findAll(spec);
    }

    public static class AppController {
    }
}
