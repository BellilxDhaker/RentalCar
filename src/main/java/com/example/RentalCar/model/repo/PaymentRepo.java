package com.example.RentalCar.model.repo;

import com.example.RentalCar.model.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {

    List<Payment> findByUserId(Long userId);
}
