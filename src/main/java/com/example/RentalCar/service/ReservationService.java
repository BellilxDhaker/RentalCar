package com.example.RentalCar.service;

import com.example.RentalCar.exception.ReservationNotFoundException;
import com.example.RentalCar.model.entities.Extras;
import com.example.RentalCar.model.entities.Reservation;
import com.example.RentalCar.model.repo.ReservationRepo;
import com.example.RentalCar.model.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepo reservationRepository;

    @Autowired
    private UserRepo userRepository;

    public Reservation createOrUpdateReservation(Reservation reservation, Map<Extras, Integer> extras) {
        for (Map.Entry<Extras, Integer> entry : extras.entrySet()) {
            reservation.addExtra(entry.getKey(), entry.getValue());
        }
        reservation.calculateTotalAmount();
        return reservationRepository.save(reservation);
    }
    public Reservation accessBooking(String email, Integer reservationID) throws ReservationNotFoundException, IllegalArgumentException {
        // First, fetch the reservation based on the reservation ID
        Reservation reservation = reservationRepository.findById(reservationID)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found for ID: " + reservationID));

        // Then, check if the reservation's user email matches the provided email
        if (!reservation.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("Reservation does not belong to the user with email " + email);
        }

        // Return the reservation if everything is valid
        return reservation;
    }

}