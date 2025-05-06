package com.example.RentalCar.model.DTO;

import com.example.RentalCar.model.entities.Payment;
import java.util.List;

public class PaymentResult {

    private String status;
    private String transactionId;
    private String message;
    private Payment payment;  // Only a single payment, not a list

    // Constructor for a single payment
    public PaymentResult(String status, String transactionId, String message, Payment payment) {
        this.status = status != null ? status : "FAILED";
        this.transactionId = transactionId != null ? transactionId : "UNKNOWN";
        this.message = message != null ? message : "No details available";
        this.payment = payment; // Can be null or a valid Payment object
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
