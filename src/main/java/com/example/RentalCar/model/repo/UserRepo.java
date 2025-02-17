package com.example.RentalCar.model.repo;

import com.example.RentalCar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo  extends JpaRepository<User,Long> {
}
