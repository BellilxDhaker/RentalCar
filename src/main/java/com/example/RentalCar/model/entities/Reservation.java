package com.example.RentalCar.model.entities;


import javax.persistence.*;
import java.util.Date;
import java.time.temporal.ChronoUnit;


@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationID;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private String locationPickup = "Default Pickup";
    private String locationReturn = "Default Return";

    private Double totalAmount = 0.0;

    @Enumerated(EnumType.STRING)
    private ProtectionType protection = ProtectionType.BASIC;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicleID")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;






    public enum ProtectionType {
        BASIC(0.0),
        MEDIUM(29.97),
        PREMIUM(34.97);

        private final double dailyRate;

        ProtectionType(double dailyRate) {
            this.dailyRate = dailyRate;
        }

        public double getDailyRate() {
            return dailyRate;
        }
    }

    public Reservation() {}

    public Reservation(Integer reservationID, Date startDate, Date endDate,
                       String locationPickup, String locationReturn,
                       Vehicle vehicle, User user, ProtectionType protection) {
        this.reservationID = reservationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationPickup = locationPickup != null ? locationPickup : "Default Pickup";
        this.locationReturn = locationReturn != null ? locationReturn : "Default Return";
        this.vehicle = vehicle;
        this.user = user;
        this.protection = protection != null ? protection : ProtectionType.BASIC;
        calculateTotalAmount();
    }

    public void calculateTotalAmount() {
        if (startDate != null && endDate != null && protection != null && vehicle != null) {
            long days = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
            days = Math.max(1, days);
            double vehicleDailyRate = vehicle.getPricePerDay() != null ? vehicle.getPricePerDay() : 0.0;
            totalAmount = days * (vehicleDailyRate + protection.getDailyRate());
        } else {
            totalAmount = 0.0;
        }
    }

    // Getters
    public Integer getReservationID() {
        return reservationID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLocationPickup() {
        return locationPickup;
    }

    public String getLocationReturn() {
        return locationReturn;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public ProtectionType getProtection() {
        return protection;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setReservationID(Integer reservationID) {
        this.reservationID = reservationID;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        calculateTotalAmount();
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        calculateTotalAmount();
    }

    public void setLocationPickup(String locationPickup) {
        this.locationPickup = locationPickup != null ? locationPickup : "Default Pickup";
    }

    public void setLocationReturn(String locationReturn) {
        this.locationReturn = locationReturn != null ? locationReturn : "Default Return";
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : 0.0;
    }

    public void setProtection(ProtectionType protection) {
        this.protection = protection != null ? protection : ProtectionType.BASIC;
        calculateTotalAmount();
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        calculateTotalAmount();
    }

    public void setUser(User user) {
        this.user = user;
    }
}