package com.example.RentalCar.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentID;

    @ManyToOne
    @JoinColumn(name = "reservation_id", referencedColumnName = "reservationID")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public Payment() {
    }

    public Payment( Reservation reservation, User user) {
        this.reservation = reservation;
        this.user = user;
    }

    public Integer getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Integer paymentID) {
        this.paymentID = paymentID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "Payment{paymentID=" + paymentID + ", reservationId=" + (reservation != null ? reservation.getReservationID() : null) + ", userId=" + (user != null ? user.getId() : null) + "}";
    }
}
