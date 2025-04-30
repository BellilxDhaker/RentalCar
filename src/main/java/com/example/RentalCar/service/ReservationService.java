package com.example.RentalCar.service;

import com.example.RentalCar.model.entities.Extras;
import com.example.RentalCar.model.entities.Reservation;
import com.example.RentalCar.model.repo.ReservationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepo reservationRepository;

    public Reservation createOrUpdateReservation(Reservation reservation, Map<Extras, Integer> extras) {
        for (Map.Entry<Extras, Integer> entry : extras.entrySet()) {
            reservation.addExtra(entry.getKey(), entry.getValue());
        }
        reservation.calculateTotalAmount();
        return reservationRepository.save(reservation);
    }
}