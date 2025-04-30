package com.example.RentalCar.model.repo;


import com.example.RentalCar.model.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Integer> {
    @Query("SELECT r FROM Reservation r WHERE r.vehicle.vehicleID = :vehicleId AND " +
            "((:startDate BETWEEN r.startDate AND r.endDate) OR " +
            "(:endDate BETWEEN r.startDate AND r.endDate) OR " +
            "(r.startDate BETWEEN :startDate AND :endDate) OR " +
            "(r.endDate BETWEEN :startDate AND :endDate))")
    List<Reservation> findOverlappingReservations(
            @Param("vehicleId") Integer vehicleId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

}
