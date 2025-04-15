package com.example.RentalCar.model.repo;

import com.example.RentalCar.model.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface VehicleRepo extends JpaRepository<Vehicle,Integer>, JpaSpecificationExecutor<Vehicle> {
}
