package com.example.RentalCar.model.entities;


public enum Extras {
    ADDITIONAL_DRIVER(16.9, true),
    INFANT_SEAT(15.79, true),
    CHILD_SEAT(7.39, true),
    BOOSTER_SEAT(3.49, true),
    NAVIGATION_SYSTEM(22.0, false),
    REFUELING_SERVICE(22.0, false),
    MOBILITY_SERVICE(22.0, false),
    INTERIOR_PROTECTION(22.0, false);

    private final double price;
    private final boolean multi;

    Extras(double price, boolean multi) {
        this.price = price;
        this.multi = multi;
    }

    public double getPrice() {
        return price;
    }

    public boolean isMulti() {
        return multi;
    }
}
