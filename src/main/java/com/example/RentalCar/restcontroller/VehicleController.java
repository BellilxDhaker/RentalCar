package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.entities.Vehicle;
import com.example.RentalCar.model.repo.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);

        if (existingVehicle.isPresent()) {
            Vehicle vehicle = existingVehicle.get();

            // Update the fields with the new values
            vehicle.setBrand(updatedVehicle.getBrand());
            vehicle.setCategory(updatedVehicle.getCategory());
            vehicle.setImageURL(updatedVehicle.getImageURL());
            vehicle.setManufacturingYear(updatedVehicle.getManufacturingYear());
            vehicle.setModel(updatedVehicle.getModel());
            vehicle.setNumberOfSeats(updatedVehicle.getNumberOfSeats());
            vehicle.setPricePerDay(updatedVehicle.getPricePerDay());
            vehicle.setAvailability(updatedVehicle.getAvailability());

            // Save the updated vehicle
            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            return ResponseEntity.ok(savedVehicle); // Return updated vehicle
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if the vehicle doesn't exist
        }
    }

}
