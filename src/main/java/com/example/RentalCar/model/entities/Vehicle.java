package com.example.RentalCar.model.entities;


import javax.persistence.*;

@Entity
@Table(name = "vehicles")

public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleID;

    private String model;

    private String brand;

    private Double pricePerDay;

    private Boolean availability;

    private String imageURL;

    private Integer manufacturingYear;

    private String category;

    private Integer numberOfSeats;
    private String transmission;

    public Vehicle() {
    }

    public Vehicle(Integer vehicleID, String model, String brand, Double pricePerDay, Boolean availability, String imageURL, Integer manufacturingYear, String category, Integer numberOfSeats, String transmission) {
        this.vehicleID = vehicleID;
        this.model = model;
        this.brand = brand;
        this.pricePerDay = pricePerDay;
        this.availability = availability;
        this.imageURL = imageURL;
        this.manufacturingYear = manufacturingYear;
        this.category = category;
        this.numberOfSeats = numberOfSeats;
        this.transmission = transmission;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setManufacturingYear(Integer manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Integer getVehicleID() {
        return vehicleID;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Integer getManufacturingYear() {
        return manufacturingYear;
    }

    public String getCategory() {
        return category;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }
    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }
}
