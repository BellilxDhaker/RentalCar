package com.example.RentalCar.model.entities;


import javax.persistence.*;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_extras", joinColumns = @JoinColumn(name = "reservation_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "quantity")
    private Map<Extras, Integer> extras = new HashMap<>();


    private String status;



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
                       Vehicle vehicle, User user, ProtectionType protection,String status) {
        this.reservationID = reservationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationPickup = locationPickup != null ? locationPickup : "Default Pickup";
        this.locationReturn = locationReturn != null ? locationReturn : "Default Return";
        this.vehicle = vehicle;
        this.user = user;
        this.protection = protection != null ? protection : ProtectionType.BASIC;
        this.status = status != null ? status : "PENDING";
        calculateTotalAmount();
    }

    public void calculateTotalAmount() {
        if (startDate != null && endDate != null && protection != null && vehicle != null) {
            long days = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
            days = Math.max(1, days);
            double vehicleDailyRate = vehicle.getPricePerDay() != null ? vehicle.getPricePerDay() : 0.0;
            totalAmount = days * (vehicleDailyRate + protection.getDailyRate());

            for (Map.Entry<Extras, Integer> entry : extras.entrySet()) {
                totalAmount += entry.getKey().getPrice() * entry.getValue();
            }
        } else {
            totalAmount = 0.0;
        }
    }

    public void addExtra(Extras extra, int quantity) {
        if (extra == null || quantity <= 0) return;

        if (extra.isMulti()) {
            int currentMultiCount = extras.entrySet().stream()
                    .filter(e -> e.getKey().isMulti())
                    .mapToInt(Map.Entry::getValue)
                    .sum();

            int currentQty = extras.getOrDefault(extra, 0);
            int newTotal = currentMultiCount - currentQty + quantity;

            if (newTotal > 5) {
                throw new IllegalArgumentException("Cannot add more than 5 total multi-select extras.");
            }

            extras.put(extra, quantity);
        } else {
            extras.put(extra, 1); // Single-select can only be 1
        }

        calculateTotalAmount();
    }

    public void removeExtra(Extras extra) {
        if (extras.containsKey(extra)) {
            extras.remove(extra);
            calculateTotalAmount();
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

    public Map<Extras, Integer> getExtras() {
        return extras;
    }

    public void setExtras(Map<Extras, Integer> extras) {
        this.extras = extras;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}