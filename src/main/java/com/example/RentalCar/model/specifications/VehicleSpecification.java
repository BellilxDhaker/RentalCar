package com.example.RentalCar.model.specifications;

import com.example.RentalCar.model.entities.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecification {

    public static Specification<Vehicle> hasSeats(Integer seats) {
        return (root, query, cb) -> seats == null ? null : cb.equal(root.get("numberOfSeats"), seats);
    }

    public static Specification<Vehicle> hasTransmission(String transmission) {
        return (root, query, cb) -> (transmission == null || transmission.isEmpty()) ? null : cb.equal(root.get("transmission"), transmission);
    }

    public static Specification<Vehicle> hasCategory(String category) {
        return (root, query, cb) -> (category == null || category.isEmpty()) ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Vehicle> sortByPriceAsc() {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("pricePerDay")));
            return null;
        };
    }

    public static Specification<Vehicle> sortByPriceDesc() {
        return (root, query, cb) -> {
            query.orderBy(cb.desc(root.get("pricePerDay")));
            return null;
        };
    }
}
